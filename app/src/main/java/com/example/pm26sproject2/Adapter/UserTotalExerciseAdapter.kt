package com.example.pm26sproject2.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.R
import com.example.pm26sproject2.entity.UserExerciseTotal
import org.w3c.dom.Text

class UserTotalExerciseAdapter(
    private val users: List<UserExerciseTotal>,
    private val onItemClick: (UserExerciseTotal) -> Unit
) : RecyclerView.Adapter<UserTotalExerciseAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTotalCalories: TextView = itemView.findViewById(R.id.tvTotalCalories)
        val tvTotalSteps: TextView = itemView.findViewById(R.id.tvTotalSteps)
        val tvTotalDuration: TextView = itemView.findViewById(R.id.tvTotalDuration)

        fun bind(user: UserExerciseTotal) {

            Log.d("UserViewHolder", "User: ${user.userId}")
            Log.d("UserViewHolder", "TextViews Initialized: ${tvUserName != null && tvTotalCalories != null && tvTotalSteps != null && tvTotalDuration != null}")

            tvUserName.text = "User: ${user.userName}"
            tvTotalCalories.text = "Calories: ${user.calories}"
            tvTotalSteps.text = "Steps: ${user.steps}"
            tvTotalDuration.text = "Duration: ${user.duration} min"
            itemView.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // aqui garantimos que o layout correto seja inflado
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_total_user_dialog, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}

