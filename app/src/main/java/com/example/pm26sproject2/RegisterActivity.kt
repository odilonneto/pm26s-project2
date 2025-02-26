package com.example.pm26sproject2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private val calendar = Calendar.getInstance()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        birthdateEditText = findViewById(R.id.birthdateEditText)

        val tvGoToLogin: TextView = findViewById(R.id.tvGoToLogin)
        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                birthdateEditText.setText(format.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String, birthdate: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    val userDataMap = mapOf(
                        "id" to userId,
                        "name" to name,
                        "birthdate" to birthdate,
                        "email" to email
                    )

                    db.collection("User")
                        .document(userId.toString())
                        .set(userDataMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration completed successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Log.w(TAG, "Error creating user.", task.exception)
                Toast.makeText(this, "Error in registration: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun btRegisterOnClick(view: View) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val birthdate = birthdateEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo nome", Toast.LENGTH_SHORT).show()
        } else if (birthdate.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo data de aniversário", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo senha", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo senha com pelo menos 6 digitos", Toast.LENGTH_SHORT).show()
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Preencha o campo senha", Toast.LENGTH_SHORT).show()
        } else if (password == confirmPassword) {
            Toast.makeText(this@RegisterActivity, "As senhas não combinam", Toast.LENGTH_SHORT).show()
        } else {
            createUserWithEmailAndPassword(email, password, name, birthdate)
            Toast.makeText(
                this@RegisterActivity,
                "Account created successfully.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
