package com.example.pm26sproject2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.R
import com.example.pm26sproject2.entity.UserExerciseTotal

class UserTotalExerciseAdapter(
    private val users: List<UserExerciseTotal>,
    private val onItemClick: (UserExerciseTotal) -> Unit
) : RecyclerView.Adapter<UserTotalExerciseAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: UserExerciseTotal) {
            itemView.findViewById<TextView>(R.id.tvTotalCalories).text = "Calories: ${user.calories}"
            itemView.findViewById<TextView>(R.id.tvTotalSteps).text = "Steps: ${user.steps}"
            itemView.findViewById<TextView>(R.id.tvTotalDuration).text = "Duration: ${user.duration} min"

            itemView.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_exercise, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}
