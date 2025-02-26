package com.example.pm26sproject2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val name = nameEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        val groupRef = db.collection("Groups").document() // Gerando ID automático
        val groupId = groupRef.id

        val groupDataMap = mapOf(
            "groupId" to groupId,
            "groupCreatorId" to userId,
            "groupName" to name,
            "groupDescription" to description
        )

        groupRef.set(groupDataMap)
            .addOnSuccessListener {
                Log.d("Firebase", "Grupo salvo com sucesso!")

                // Adiciona associação do usuário ao grupo na collection "UserGroups"
                val userGroupMap = mapOf(
                    "userId" to userId,
                    "groupId" to groupId
                )

                db.collection("UserGroups")
                    .add(userGroupMap)
                    .addOnSuccessListener {
                        Toast.makeText(this@RegisterGroupActivity, "Grupo criado com sucesso", Toast.LENGTH_SHORT).show()
                        Log.d("Firebase", "Associação usuário-grupo criada com sucesso!")

                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Erro ao associar usuário ao grupo", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erro ao salvar o grupo", e)
            }
    }

}