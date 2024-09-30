package com.example.fitproplus_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.R
import com.example.fitproplus_final.UserRanking

class LeaderboardAdapter(private val leaderboardList: List<UserRanking>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val workoutCountTextView: TextView = view.findViewById(R.id.workoutCountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = leaderboardList[position]
        holder.usernameTextView.text = user.username
        holder.workoutCountTextView.text = "Workouts: ${user.workoutCount}"
    }

    override fun getItemCount(): Int {
        return leaderboardList.size
    }
}
