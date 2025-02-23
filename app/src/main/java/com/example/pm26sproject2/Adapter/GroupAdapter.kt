package com.example.pm26sproject2.Adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

            itemView.findViewById<TextView>(R.id.tvName).text = name
            itemView.findViewById<TextView>(R.id.tvDescription).text = description

            itemView.setOnClickListener {
                onItemClick(group)
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
