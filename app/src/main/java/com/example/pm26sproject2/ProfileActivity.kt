package com.example.pm26sproject2

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var user: FirebaseUser

    private lateinit var nameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var birthdateEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var newPasswordEditText: EditText
    private lateinit var oldPasswordEditText: EditText
    private val calendar = Calendar.getInstance()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

        nameEditText = findViewById(R.id.etName)
        emailTextView = findViewById(R.id.tvEmail)
        birthdateEditText = findViewById(R.id.birthdateEditText)
        saveButton = findViewById(R.id.btnSave)
        changePasswordButton = findViewById(R.id.btnChangePassword)
        newPasswordEditText = findViewById(R.id.etNewPassword)
        oldPasswordEditText = findViewById(R.id.etOldPassword)

        loadUserData()

        saveButton.setOnClickListener {
            saveUserData()
        }

        changePasswordButton.setOnClickListener {
            changePassword()
        }

        birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        loadUserData()
    }

    private fun loadUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("User")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        nameEditText.setText( document.getString("name") )
                        birthdateEditText.setText( document.getString("birthdate")  )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erro ao recuperar usuario", e)
            }

        emailTextView.text = user.email
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

    private fun saveUserData() {
        val name = nameEditText.text.toString()
        val birthdate = birthdateEditText.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this@ProfileActivity, "Preencha o campo nome", Toast.LENGTH_SHORT).show()
        } else if (birthdate.isEmpty()) {
            Toast.makeText(this@ProfileActivity, "Preencha o campo data de aniversário", Toast.LENGTH_SHORT).show()
        } else {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val updatedDataMap = mapOf(
                "name" to name,
                "birthdate" to birthdate
            )

            db.collection("User")
                .document(userId.toString())
                .update(updatedDataMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating data.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun changePassword() {
        val oldPassword = oldPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this@ProfileActivity, "Fill in the fields correctly.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Error changing password.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@ProfileActivity, "Incorrect current password.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
