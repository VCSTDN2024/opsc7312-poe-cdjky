data class NutritionixResponse(
    val hits: List<FoodItem>
)

data class FoodItem(
    val fields: FoodFields
)

data class FoodFields(
    val item_name: String,
    val brand_name: String,
    val nf_calories: Float
)
