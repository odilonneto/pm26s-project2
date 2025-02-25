package com.example.pm26sproject2.Adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.GroupActivity
import com.example.pm26sproject2.R
import com.example.pm26sproject2.entity.Group

class GroupAdapter(
    private val groups: List<Group>,
    private val onItemClick: (Group) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(group: Group) {
            val name = group.groupName
            val description = group.groupDescription
            val groupId = group.groupId // Certifique-se de que o modelo Group tem esse campo

            itemView.findViewById<TextView>(R.id.tvName).text = name
            itemView.findViewById<TextView>(R.id.tvDescription).text = description

            itemView.findViewById<Button>(R.id.btAcessarGrupo).setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, GroupActivity::class.java)
                intent.putExtra("GROUP_ID", groupId)
                context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_exercise, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size
}
