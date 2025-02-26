package com.example.pm26sproject2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.Adapter.UserTotalExerciseAdapter
import com.example.pm26sproject2.entity.UserExerciseTotal
import com.google.firebase.firestore.FirebaseFirestore

class GroupActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserTotalExerciseAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<UserExerciseTotal>()
    private var groupId: String? = null  // ID do grupo atual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        // Recebendo o ID do grupo via Intent
        groupId = intent.getStringExtra("GROUP_ID")

        recyclerView = findViewById(R.id.recyclerViewGroupUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAddUser = findViewById<Button>(R.id.btnAddUser)
        btnAddUser.setOnClickListener {
            showAddUserDialog()
        }

        groupId?.let {
            fetchGroupUsers(it)
        }
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)

        AlertDialog.Builder(this)
            .setTitle("Adicionar Usuário")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val email = editEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    addUserToGroup(email)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addUserToGroup(email: String) {
        // Buscar usuário pelo email
        db.collection("User")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.documents[0]
                    val userId = user.id

                    // Adicionar o usuário ao grupo
                    groupId?.let { groupId ->
                        db.collection("UserGroups")
                            .add(mapOf("groupId" to groupId, "userId" to userId))
                            .addOnSuccessListener {
                                Toast.makeText(this@GroupActivity, "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                Log.d("Firestore", "Usuário adicionado ao grupo!")
                                fetchGroupUsers(groupId) // Atualiza a lista
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@GroupActivity, "Erro ao adicionar usuário", Toast.LENGTH_SHORT).show()
                                Log.e("Firestore", "Erro ao adicionar usuário", e)
                            }
                    }
                } else {
                    Toast.makeText(this@GroupActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Usuário não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar usuário", e)
            }
    }

    private fun fetchGroupUsers(groupId: String) {
        db.collection("UserGroups")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val userIds = documents.mapNotNull { it.getString("userId") }
                fetchUserExerciseTotals(userIds)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar usuários do grupo", e)
            }
    }

    private fun fetchUserExerciseTotals(userIds: List<String>) {
        userList.clear()

        if (userIds.isEmpty()) {
            adapter = UserTotalExerciseAdapter(userList) { }
            recyclerView.adapter = adapter
            return
        }

        val usersRef = db.collection("User").whereIn("id", userIds).get()

// Criar um mapa para armazenar os nomes dos usuários (id -> userName)
        val userNameMap = mutableMapOf<String, String>()

// Primeiro, obtenha os dados dos usuários e mapeie o id para o nome
        usersRef.addOnSuccessListener { userDocuments ->
            for (document in userDocuments) {
                val userId = document.getString("id") ?: continue
                val userName = document.getString("name") ?: "Unknown"
                userNameMap[userId] = userName
            }

            // Agora que temos o mapa userNameMap, carregue as atividades
            val activitiesRef = db.collection("Activities")

            activitiesRef
                .whereIn("userId", userIds)
                .get()
                .addOnSuccessListener { documents ->
                    val userTotalsMap = mutableMapOf<String, UserExerciseTotal>()

                    for (document in documents) {
                        val userId = document.getString("userId") ?: continue
                        val calories = document.getDouble("calories")?.toInt() ?: 0
                        val steps = document.getLong("steps")?.toInt() ?: 0
                        val duration = document.getLong("duration")?.toInt() ?: 0

                        // Obtenha o nome do usuário a partir do mapa
                        val userName = userNameMap[userId] ?: "Unknown"

                        // Se o usuário já está no mapa, soma os valores
                        val existingTotal = userTotalsMap[userId] ?: UserExerciseTotal(userId, userName, 0.0, 0, 0)
                        userTotalsMap[userId] = existingTotal.copy(
                            userName = userName,
                            calories = existingTotal.calories + calories,
                            steps = existingTotal.steps + steps,
                            duration = existingTotal.duration + duration
                        )
                    }

                    try {
                        Log.d("Debug", "Ordenando lista...")
                        userList.addAll(userTotalsMap.values.sortedByDescending { it.calories })

                        Log.d("Debug", "Criando adapter...")
                        adapter = UserTotalExerciseAdapter(userList) { selectedUser ->
                            Log.d("Ranking", "User: ${selectedUser.userId}, Calorias: ${selectedUser.calories}")
                        }

                        Log.d("Debug", "Definindo adapter no RecyclerView...")
                        recyclerView.adapter = adapter

                        Log.d("Debug", "Adapter definido com sucesso!")
                    } catch (e: Exception) {
                        Log.e("RankingError", "Erro ao processar lista de usuários", e)
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao buscar atividades dos usuários", e)
                }
        }

//        val usersRef = db.collection("User").whereIn("id", userIds).get();
//
//        val userTotalsMap = mutableMapOf<String, UserExerciseTotal>()
//        val activitiesRef = db.collection("Activities")
//
//        activitiesRef
//            .whereIn("userId", userIds)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val userId = document.getString("userId") ?: continue
//                    val calories = document.getDouble("calories")?.toInt() ?: 0
//                    val steps = document.getLong("steps")?.toInt() ?: 0
//                    val duration = document.getLong("duration")?.toInt() ?: 0
//
//                    // Se o usuário já está no mapa, soma os valores
//                    val existingTotal = userTotalsMap[userId] ?: UserExerciseTotal(userId, "userName", 0.0, 0, 0)
//                    userTotalsMap[userId] = existingTotal.copy(
//                        userName = "userName",
//                        calories = existingTotal.calories + calories,
//                        steps = existingTotal.steps + steps,
//                        duration = existingTotal.duration + duration
//                    )
//                }
//
//                try {
//                    Log.d("Debug", "Ordenando lista...")
//                    userList.addAll(userTotalsMap.values.sortedByDescending { it.calories })
//
//                    Log.d("Debug", "Criando adapter...")
//                    adapter = UserTotalExerciseAdapter(userList) { selectedUser ->
//                        Log.d("Ranking", "User: ${selectedUser.userId}, Calorias: ${selectedUser.calories}")
//                    }
//
//                    Log.d("Debug", "Definindo adapter no RecyclerView...")
//                    recyclerView.adapter = adapter
//
//                    Log.d("Debug", "Adapter definido com sucesso!")
//                } catch (e: Exception) {
//                    Log.e("RankingError", "Erro ao processar lista de usuários", e)
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Erro ao buscar atividades dos usuários", e)
//            }
    }

}
