package com.example.pm26sproject2

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow
import kotlin.math.sqrt

class MonitorActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var activityStatus: TextView
    private lateinit var stepsCount: TextView
    private lateinit var caloriesBurned: TextView

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var steps = 0
    private var calories = 0.0

    private var lastAcceleration = 0f
    private var currentAcceleration = 0f
    private var acceleration = 0f
    private var lastTimestamp = 0L

    private var startTime = 0L

    private val threshold = 1.5f // limiar de aceleração maior para movimento
    private val timeThreshold = 500 // milissegundos, para evitar múltiplos passos rápidos demais

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        activityStatus = findViewById(R.id.activityStatus)
        stepsCount = findViewById(R.id.stepsCount)
        caloriesBurned = findViewById(R.id.caloriesBurned)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        startButton.setOnClickListener {
            startMonitoring()
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }
    }

    private fun startMonitoring() {
        activityStatus.text = "Atividade em andamento..."
        startButton.isEnabled = false
        stopButton.isEnabled = true

        startTime = System.currentTimeMillis();

        // registrar o listener do acelerômetro
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun stopMonitoring() {
        activityStatus.text = "Atividade encerrada."
        startButton.isEnabled = true
        stopButton.isEnabled = false

        // parar de ouvir o sensor
        sensorManager?.unregisterListener(this)

        // salvar os dados no Firebase
        saveActivityDataToFirebase()
    }

    private fun saveActivityDataToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // so se tiver o usuario
        userId?.let {
            val endTime = System.currentTimeMillis();
            val activityDataMap = mapOf(
                "startTime" to startTime,
                "endTime" to endTime,
                "duration" to endTime - startTime, // em milisegundos
                "userId" to userId,
                "steps" to steps,
                "calories" to calories
            )

            db.collection("Activities")
                .document(userId + "_" + System.currentTimeMillis()) // como se fosse o "id"
                .set(activityDataMap)
                .addOnSuccessListener {
                    Log.d("Firebase", "Atividade salva com sucesso!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao salvar a atividade", e)
                }
        }
    }

    private fun updateUI() {
        stepsCount.text = "Passos: $steps"
        caloriesBurned.text = "Calorias: %.2f".format(calories)
    }

    private fun calculateStepsAndCalories(accelerometerData: SensorEvent) {
        val x = accelerometerData.values[0]
        val y = accelerometerData.values[1]
        val z = accelerometerData.values[2]

        // calcular a aceleração total
        currentAcceleration = sqrt(x * x + y * y + z * z)
        acceleration = currentAcceleration - lastAcceleration
        lastAcceleration = currentAcceleration

        // verificar se a aceleração é significativa
        val currentTime = System.currentTimeMillis()

        Log.e("SensorService", "Verificando se vai contar!")
        if (acceleration > threshold && (currentTime - lastTimestamp > timeThreshold)) {
            Log.e("SensorService", "========= > Vai contar!")
            steps++
            calories += 0.04 // supondo 0.04 calorias por passo
            lastTimestamp = currentTime
            updateUI()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.e("SensorService", "onSensorChanged!")
        if (event != null) {
            Log.e("SensorService", "event is not null!")
            calculateStepsAndCalories(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não usamos a precisão
    }

    override fun onDestroy() {
        super.onDestroy()
        // remover o listener do acelerômetro quando a activity for destruída
        sensorManager?.unregisterListener(this)
    }
}
