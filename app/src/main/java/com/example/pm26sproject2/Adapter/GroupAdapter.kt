package com.example.pm26sproject2.Adapter


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.GroupActivity
import com.example.pm26sproject2.R
import com.example.pm26sproject2.entity.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroupAdapter(
    private val groups: List<Group>,
    private val onItemClick: (Group) -> Unit,
    private val onGroupDeleted: () -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(group: Group) {
            val name = group.groupName
            val description = group.groupDescription
            val groupId = group.groupId
            val creatorId = group.groupCreatorId

            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
            val btnViewGroup = itemView.findViewById<Button>(R.id.btViewGroup)
            val btnDeleteGroup = itemView.findViewById<Button>(R.id.btnDeleteGroup)

            tvName.text = name
            tvDescription.text = description

            // Obtém o ID do usuário logado
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // Exibe ou esconde o botão de excluir
            if (userId == creatorId) {
                btnDeleteGroup.visibility = View.VISIBLE
            } else {
                btnDeleteGroup.visibility = View.GONE
            }

            btnViewGroup.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, GroupActivity::class.java)
                intent.putExtra("GROUP_ID", groupId)
                context.startActivity(intent)
            }

            btnDeleteGroup.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Excluir Grupo")
                    .setMessage("Tem certeza que deseja excluir este grupo?")
                    .setPositiveButton("Sim") { _, _ ->
                        deleteGroup(groupId)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
        private fun deleteGroup(groupId: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection("Groups").document(groupId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Grupo excluído com sucesso!")

                    onGroupDeleted()
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao excluir o grupo", e)
                }

            db.collection("UserGroups")
                .document(groupId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Relação usuário grupo excluído com sucesso!")

                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao excluir relação usuario grupo", e)
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size
}
