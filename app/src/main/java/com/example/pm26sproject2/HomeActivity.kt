package com.example.pm26sproject2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.Adapter.ExerciseAdapter
import com.example.pm26sproject2.entity.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private var isMenuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        InitListExercises()

        val btnMenu: Button = findViewById(R.id.btnMenu)
        val sideMenu: LinearLayout = findViewById(R.id.sideMenu)

        btnMenu.setOnClickListener {
            toggleMenu(sideMenu)
        }

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnProfile: Button = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val btnStartActivity: Button = findViewById(R.id.btnStartActivity)
        btnStartActivity.setOnClickListener {
            val intent = Intent(this, MonitorActivity::class.java)
            startActivity(intent)
        }

        val btnGroups: Button = findViewById(R.id.btnGroups)
        btnGroups.setOnClickListener {
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleMenu(menu: View) {
        if (isMenuVisible) {
            menu.animate()
                .translationX(menu.width.toFloat())
                .setDuration(300)
                .withEndAction {
                    menu.visibility = View.GONE
                }
                .start()
        } else {
            menu.visibility = View.VISIBLE
            menu.animate()
                .translationX(600f)
                .setDuration(300)
                .start()
        }
        isMenuVisible = !isMenuVisible
    }

    private fun InitListExercises() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            db.collection("Activities")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val exercises: MutableList<Exercise> = mutableListOf()

                        for (document in querySnapshot) {
                            val startTime = document.getLong("startTime") ?: 0
                            val endTime = document.getLong("endTime") ?: 0
                            val duration = document.getLong("duration") ?: 0
                            val steps = document.getLong("steps") ?: 0
                            val calories = document.getDouble("calories") ?: 0

                            val exercise = Exercise(
                                userId.toString(),
                                calories.toDouble(),
                                duration.toLong(),
                                startTime.toLong(),
                                endTime.toLong(),
                                steps.toInt()
                            )

                            exercises.add(exercise)
                        }

                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.layoutManager = GridLayoutManager(this, 1)
                        recyclerView.adapter = ExerciseAdapter(exercises, {})
                    } else {
                        Log.d("Firebase", "Nenhuma atividade encontrada para este usuário.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao recuperar atividades", e)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        InitListExercises()
    }
}