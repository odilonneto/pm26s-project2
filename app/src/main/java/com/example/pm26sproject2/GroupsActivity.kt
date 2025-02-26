package com.example.pm26sproject2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.Adapter.ExerciseAdapter
import com.example.pm26sproject2.Adapter.GroupAdapter
import com.example.pm26sproject2.entity.Exercise
import com.example.pm26sproject2.entity.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroupsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        InitListGroups()

    }

    fun btNovoGrupoOnClick(view: View) {
        val intent = Intent(this, RegisterGroupActivity::class.java)
        startActivity(intent)
    }

    private fun InitListGroups() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (!userId.isNullOrEmpty()) {
            val db = FirebaseFirestore.getInstance()

            // Primeiro, buscar os groupId do usuário na coleção UserGroups
            db.collection("UserGroups")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val groupIds = querySnapshot.documents.mapNotNull { it.getString("groupId") }

                        if (groupIds.isNotEmpty()) {
                            db.collection("Groups")
                                .whereIn("groupId", groupIds)
                                .get()
                                .addOnSuccessListener { groupSnapshot ->
                                    if (!groupSnapshot.isEmpty) {
                                        val groups = mutableListOf<Group>()

                                        for (document in groupSnapshot) {
                                            val group = Group(
                                                document.getString("groupId") ?: "",
                                                document.getString("groupName") ?: "",
                                                document.getString("groupDescription") ?: "",
                                                document.getString("groupCreatorId") ?: ""
                                            )
                                            groups.add(group)
                                        }

                                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                                        recyclerView.layoutManager = GridLayoutManager(this, 1)
                                        recyclerView.adapter = GroupAdapter(groups, {}, ::InitListGroups)
                                    } else {
                                        Log.d("Firebase", "Nenhum grupo encontrado para este usuário.")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firebase", "Erro ao recuperar grupos", e)
                                }
                        }
                    } else {
                        Log.d("Firebase", "Nenhuma associação de grupo encontrada para este usuário.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao recuperar UserGroups", e)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        InitListGroups()
    }

}