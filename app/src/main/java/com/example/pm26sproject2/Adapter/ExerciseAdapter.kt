package com.example.pm26sproject2.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sproject2.R
import com.example.pm26sproject2.entity.Exercise
import java.text.SimpleDateFormat
import java.util.Date

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onItemClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("DefaultLocale", "SimpleDateFormat")
        fun bind(exercise: Exercise) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

            itemView.findViewById<TextView>(R.id.tvSteps).text = exercise.steps.toString()
            itemView.findViewById<TextView>(R.id.tvCalories).text = String.format("%.2f", exercise.calories)

            val startTime = Date(exercise.startTime)
            itemView.findViewById<TextView>(R.id.tvStartTime).text = sdf.format(startTime)

            val endTime = Date(exercise.endTime)
            itemView.findViewById<TextView>(R.id.tvEndTime).text = sdf.format(endTime)

            val hours = exercise.duration / 3600000
            val minutes = (exercise.duration % 3600000) / 60000
            val seconds = (exercise.duration % 60000) / 1000
            itemView.findViewById<TextView>(R.id.tvDuration).text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            itemView.setOnClickListener {
                onItemClick(exercise)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}
