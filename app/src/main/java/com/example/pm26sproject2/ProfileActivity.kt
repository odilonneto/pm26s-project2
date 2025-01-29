package com.example.pm26sproject2

import android.app.DatePickerDialog
import android.os.Bundle
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
        emailTextView.text = user.email

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java)
                val birthdate = snapshot.child("birthdate").getValue(String::class.java)

                nameEditText.setText(name ?: "")
                birthdateEditText.setText(birthdate ?: "")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error loading profile data.", Toast.LENGTH_SHORT).show()
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

    private fun saveUserData() {
        val name = nameEditText.text.toString()
        val birthdate = birthdateEditText.text.toString()

        val userData = mapOf(
            "name" to name,
            "birthdate" to birthdate
        )

        database.setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
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
