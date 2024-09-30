package com.example.fitproplus_final

import NutritionixAPI
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NutritionInfoActivity : AppCompatActivity() {

    private lateinit var foodSearchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var nfTotalFat: TextView
    private lateinit var nfSaturatedFat: TextView
    private lateinit var nfCholesterol: TextView
    private lateinit var nfSodium: TextView
    private lateinit var nfTotalCarbohydrate: TextView
    private lateinit var nfDietaryFiber: TextView
    private lateinit var nfSugars: TextView
    private lateinit var nfProtein: TextView
    private lateinit var nfVitaminD: TextView
    private lateinit var nfCalcium: TextView
    private lateinit var nfIron: TextView
    private lateinit var nfPotassium: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_info)

        // View binding with findViewById
        foodSearchEditText = findViewById(R.id.foodSearchEditText)
        searchButton = findViewById(R.id.searchButton)

        // Initialize all the TextViews for nutrition info
        nfTotalFat = findViewById(R.id.nf_total_fat)
        nfSaturatedFat = findViewById(R.id.nf_saturated_fat)
        nfCholesterol = findViewById(R.id.nf_cholesterol)
        nfSodium = findViewById(R.id.nf_sodium)
        nfTotalCarbohydrate = findViewById(R.id.nf_total_carbohydrate)
        nfDietaryFiber = findViewById(R.id.nf_dietary_fiber)
        nfSugars = findViewById(R.id.nf_sugars)
        nfProtein = findViewById(R.id.nf_protein)
        nfVitaminD = findViewById(R.id.nf_vitamin_d)
        nfCalcium = findViewById(R.id.nf_calcium)
        nfIron = findViewById(R.id.nf_iron)
        nfPotassium = findViewById(R.id.nf_potassium)

        // Set up search button listener
        searchButton.setOnClickListener {
            val query = foodSearchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchFood(query)
            } else {
                Toast.makeText(this, "Please enter a food to search.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to search for food and display nutrition details
    private fun searchFood(query: String) {
        val api = RetrofitClient.retrofitInstance.create(NutritionixAPI::class.java)
        val body = mapOf("query" to query, "timezone" to "Africa/Johannesburg")

        api.getFoodInfo(body).enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val food = response.body()!!.foods.firstOrNull()
                    food?.let {
                        // Update nutrition details in TextViews
                        nfTotalFat.text = "Total Fat: ${it.nf_total_fat ?: "N/A"} g"
                        nfSaturatedFat.text = "Saturated Fat: ${it.nf_saturated_fat ?: "N/A"} g"
                        nfCholesterol.text = "Cholesterol: ${it.nf_cholesterol ?: "N/A"} mg"
                        nfSodium.text = "Sodium: ${it.nf_sodium ?: "N/A"} mg"
                        nfTotalCarbohydrate.text = "Total Carbohydrate: ${it.nf_total_carbohydrate ?: "N/A"} g"
                        nfDietaryFiber.text = "Dietary Fiber: ${it.nf_dietary_fiber ?: "N/A"} g"
                        nfSugars.text = "Sugars: ${it.nf_sugars ?: "N/A"} g"
                        nfProtein.text = "Protein: ${it.nf_protein ?: "N/A"} g"
                        nfVitaminD.text = "Vitamin D: ${it.nf_vitamin_d ?: "N/A"} µg"
                        nfCalcium.text = "Calcium: ${it.nf_calcium ?: "N/A"} mg"
                        nfIron.text = "Iron: ${it.nf_iron ?: "N/A"} mg"
                        nfPotassium.text = "Potassium: ${it.nf_potassium ?: "N/A"} mg"
                    }
                } else {
                    Toast.makeText(this@NutritionInfoActivity, "No food found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Toast.makeText(this@NutritionInfoActivity, "Failed to fetch food data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
