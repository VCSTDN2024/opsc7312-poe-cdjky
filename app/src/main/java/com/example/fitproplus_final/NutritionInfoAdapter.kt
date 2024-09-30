package com.example.fitproplus_final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NutritionInfoAdapter(private val nutritionList: List<Pair<String, String>>) :
    RecyclerView.Adapter<NutritionInfoAdapter.NutritionViewHolder>() {

    inner class NutritionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nutritionName: TextView = itemView.findViewById(R.id.nutritionName)
        val nutritionValue: TextView = itemView.findViewById(R.id.nutritionValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nutrition_item, parent, false)
        return NutritionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val nutritionItem = nutritionList[position]
        holder.nutritionName.text = nutritionItem.first
        holder.nutritionValue.text = nutritionItem.second
    }

    override fun getItemCount(): Int {
        return nutritionList.size
    }
}