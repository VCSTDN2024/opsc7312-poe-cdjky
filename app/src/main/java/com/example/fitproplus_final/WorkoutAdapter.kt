package com.example.fitproplus_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.R
import com.example.fitproplus_final.models.WorkoutLog

class WorkoutAdapter(private val workoutList: List<WorkoutLog>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.exerciseNameTextView.text = workout.exerciseName
        holder.setsTextView.text = "Sets: ${workout.sets}"
        holder.repsTextView.text = "Reps: ${workout.reps}"
        holder.durationTextView.text = "Duration: ${workout.duration} minutes"
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseNameTextView: TextView = itemView.findViewById(R.id.exerciseNameTextView)
        val setsTextView: TextView = itemView.findViewById(R.id.setsTextView)
        val repsTextView: TextView = itemView.findViewById(R.id.repsTextView)
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
    }
}
