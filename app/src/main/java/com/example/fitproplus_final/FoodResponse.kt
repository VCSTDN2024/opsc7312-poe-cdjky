package com.example.fitproplus_final

data class FoodResponse(
    val foods: List<Food>
)

data class Food(
    val food_name: String,
    val nf_calories: Double,
    val nf_total_fat: String,
    val nf_saturated_fat: String,
    val nf_cholesterol: String,
    val nf_sodium: String,
    val nf_total_carbohydrate: String,
    val nf_dietary_fiber: String,
    val nf_sugars: String,
    val nf_protein: String,
    val nf_vitamin_d: String,
    val nf_calcium: String,
    val nf_iron: String,
    val nf_potassium: String,
    val serving_qty: String,
    val serving_unit: String,
    val serving_weight_grams: Double
)


