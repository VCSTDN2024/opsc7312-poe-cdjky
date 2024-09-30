package com.example.fitproplus_final.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.MealLog
import com.example.fitproplus_final.R


data class MealLog(val foodName: String, val calories: Float)

class MealsAdapter(private val mealList: List<MealLog>) : RecyclerView.Adapter<MealsAdapter.MealViewHolder>() {

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.foodNameTextView)
        val mealCalories: TextView = itemView.findViewById(R.id.caloriesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]
        holder.mealName.text = meal.foodName
        holder.mealCalories.text = "${meal.calories} kcal"
    }

    override fun getItemCount(): Int = mealList.size
}

