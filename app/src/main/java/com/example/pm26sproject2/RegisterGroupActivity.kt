package com.example.pm26sproject2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterGroupActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextView
    private lateinit var descriptionEditText: TextView

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_group)

        nameEditText = findViewById(R.id.groupNameEditText)
        descriptionEditText = findViewById(R.id.groupDescriptionEditText)
    }

    fun btRegisterGroupOnClick(view: View) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val name = nameEditText.text
        val description = descriptionEditText.text




            val groupDataMap = mapOf(
                "groupCreatorId" to userId,
                "groupName" to name,
                "groupDescription" to description
            )

            db.collection("Groups")
                .document(userId + "_" + System.currentTimeMillis()) // como se fosse o "id"
                .set(groupDataMap)
                .addOnSuccessListener {
                    Log.d("Firebase", "Grupo salvo com sucesso!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao salvar o grupo", e)
                }

    }
}