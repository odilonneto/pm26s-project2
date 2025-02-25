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

        userId?.let {
            db.collection("Groups")
                //.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val groups: MutableList<Group> = mutableListOf()

                        for (document in querySnapshot) {
                            val groupId = document.getString("groupId")
                            val groupName = document.getString("groupName")
                            val groupDescription = document.getString("groupDescription")
                            val groupCreatorId = document.getString("groupCreatorId")

                            val group = Group(
                                groupId.toString(),
                                groupName.toString(),
                                groupDescription.toString(),
                                groupCreatorId.toString()
                            )

                            groups.add(group)
                        }

                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.layoutManager = GridLayoutManager(this, 1)
                        recyclerView.adapter = GroupAdapter(groups, {})
                    } else {
                        Log.d("Firebase", "Nenhuma atividade encontrada para este usuário.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao recuperar atividades", e)
                }
        }
    }
}