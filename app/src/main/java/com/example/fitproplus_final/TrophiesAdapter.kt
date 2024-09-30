package com.example.fitproplus_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.R

class TrophiesAdapter(private val trophiesList: List<String>) : RecyclerView.Adapter<TrophiesAdapter.TrophiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophiesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trophy, parent, false)
        return TrophiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrophiesViewHolder, position: Int) {
        holder.trophyTextView.text = trophiesList[position]
    }

    override fun getItemCount(): Int = trophiesList.size

    class TrophiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trophyTextView: TextView = itemView.findViewById(R.id.trophyTextView)
    }
}
