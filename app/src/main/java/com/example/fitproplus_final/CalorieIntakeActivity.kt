package com.example.fitproplus_final

import NutritionixAPI
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.adapters.MealsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class CalorieIntakeActivity : AppCompatActivity() {

    private lateinit var foodSearchEditText: EditText
    private lateinit var portionSizeEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var logMealButton: Button
    private lateinit var scanBarcodeButton: Button
    private lateinit var foodDetailsTextView: TextView
    private lateinit var caloriesRemainingTextView: TextView
    private lateinit var mealsRecyclerView: RecyclerView
    private lateinit var mealAdapter: MealsAdapter
    private lateinit var nutritionButton: Button
    private lateinit var backButton: Button // For navigation back to HomeActivity

    private var remainingCalories = 0f
    private var totalCaloriesConsumed = 0f
    private val mealList = mutableListOf<MealLog>()

    private val CAMERA_REQUEST_CODE = 100
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_intake)

        // View binding
        foodSearchEditText = findViewById(R.id.foodSearchEditText)
        portionSizeEditText = findViewById(R.id.portionSizeEditText)
        searchButton = findViewById(R.id.searchButton)
        logMealButton = findViewById(R.id.logMealButton)
        scanBarcodeButton = findViewById(R.id.scanBarcode)
        foodDetailsTextView = findViewById(R.id.foodDetailsTextView)
        caloriesRemainingTextView = findViewById(R.id.caloriesRemainingTextView)
        mealsRecyclerView = findViewById(R.id.mealsRecyclerView)
        nutritionButton = findViewById(R.id.nutrition)
        backButton = findViewById(R.id.backButton)

        setupRecyclerView()
        fetchUserMaintenanceCalories()
        loadLoggedMeals()  // Load only today's meals

        // Set up barcode scanner button
        scanBarcodeButton.setOnClickListener {
            if (checkCameraPermission()) {
                startBarcodeScanner()
            } else {
                requestCameraPermission()
            }
        }

        nutritionButton.setOnClickListener {
            startActivity(Intent(this, NutritionInfoActivity::class.java))
        }

        // Set up search button
        searchButton.setOnClickListener {
            val query = foodSearchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchFood(query)
            } else {
                Toast.makeText(this, "Please enter a food to search.", Toast.LENGTH_SHORT).show()
            }
        }

        // Log meal button
        logMealButton.setOnClickListener {
            logMeal()
        }

        // Back button to navigate back to HomeActivity after logging
        backButton.setOnClickListener {
            finish() // Navigate back to HomeActivity
        }

        resetDailyCalories()
    }

    private fun setupRecyclerView() {
        mealsRecyclerView.layoutManager = LinearLayoutManager(this)
        mealAdapter = MealsAdapter(mealList)
        mealsRecyclerView.adapter = mealAdapter
    }

    private fun fetchUserMaintenanceCalories() {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val docRef = firestore.collection("users").document(userId)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val maintenanceCaloriesValue = documentSnapshot.get("maintenanceCalories")

                    if (maintenanceCaloriesValue is Number) {
                        val maintenanceCalories = maintenanceCaloriesValue.toFloat()
                        val consumedToday = documentSnapshot.getDouble("caloriesConsumedToday")?.toFloat() ?: 0f
                        remainingCalories = maintenanceCalories - consumedToday
                        updateCaloriesRemaining()
                    } else {
                        // Handle case where maintenanceCalories is missing or not a number
                        Toast.makeText(this, "Maintenance calories not set correctly", Toast.LENGTH_SHORT).show()
                        remainingCalories = 0f // or set a default value
                        updateCaloriesRemaining()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve maintenance calories", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCaloriesRemaining() {
        caloriesRemainingTextView.text = "Remaining Calories: $remainingCalories kcal"
    }

    // Log meal and update calories
    private fun logMeal() {
        val caloriesPerUnit = foodDetailsTextView.text.toString().split(": ")[1].split(" ")[0].toFloatOrNull()
        val portionSize = portionSizeEditText.text.toString().toFloatOrNull() ?: 100f  // Default to 100g if empty

        if (caloriesPerUnit != null && portionSize > 0f) {
            val totalCalories = caloriesPerUnit * (portionSize / 100)
            totalCaloriesConsumed += totalCalories
            remainingCalories -= totalCalories
            updateCaloriesRemaining()

            val currentTime = System.currentTimeMillis()
            val meal = MealLog(foodDetailsTextView.text.toString(), totalCalories, currentTime)

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val firestore = FirebaseFirestore.getInstance()
                val userRef = firestore.collection("users").document(userId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val currentConsumed = snapshot.getDouble("caloriesConsumedToday")?.toFloat() ?: 0f
                    val updatedConsumed = currentConsumed + totalCalories

                    transaction.update(userRef, mapOf("caloriesConsumedToday" to updatedConsumed))
                    val mealRef = userRef.collection("mealLogs").document()
                    transaction.set(mealRef, meal)
                }.addOnSuccessListener {
                    mealList.add(meal)
                    mealAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Meal logged successfully!", Toast.LENGTH_SHORT).show()

                    // Navigate back to HomeActivity to update the graph
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent) // Start the HomeActivity after meal is logged
                    finish() // Close CalorieIntakeActivity
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to log meal", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please enter a valid portion size.", Toast.LENGTH_SHORT).show()
        }
    }

    // Only load meals logged today
    private fun loadLoggedMeals() {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val todayStart = LocalDate.now(ZoneId.of("Africa/Johannesburg")).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("mealLogs")
                .whereGreaterThan("timestamp", todayStart) // Filter by today's meals
                .get()
                .addOnSuccessListener { querySnapshot ->
                    mealList.clear()
                    for (document in querySnapshot.documents) {
                        val meal = document.toObject(MealLog::class.java)
                        if (meal != null) {
                            mealList.add(meal)
                        }
                    }
                    mealAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load meals", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startBarcodeScanner()
        } else {
            Toast.makeText(this, "Camera permission required for barcode scanning", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBarcodeScanner() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating file for image capture", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            photoUri = FileProvider.getUriForFile(
                this,
                "com.example.fitproplus_final.fileprovider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val image = InputImage.fromFilePath(this, photoUri)
            processBarcodeFromImage(image)
        }
    }

    private fun processBarcodeFromImage(image: InputImage) {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_PRODUCT -> {
                            searchFood(barcode.rawValue ?: "")
                        }
                        else -> {
                            Toast.makeText(this, "Barcode not recognized", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to scan barcode", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchFood(query: String) {
        val api = RetrofitClient.retrofitInstance.create(NutritionixAPI::class.java)
        val body = mapOf("query" to query, "timezone" to "Africa/Johannesburg")

        api.getFoodInfo(body).enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val food = response.body()!!.foods.firstOrNull()
                    food?.let {
                        foodDetailsTextView.text = "${it.food_name}: ${it.nf_calories} kcal"
                    }
                } else {
                    Toast.makeText(this@CalorieIntakeActivity, "No food found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Toast.makeText(this@CalorieIntakeActivity, "Failed to fetch food data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetDailyCalories() {
        val today = LocalDate.now(ZoneId.of("Africa/Johannesburg"))
        val todayStart = today.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))

        if (System.currentTimeMillis() / 1000 >= todayStart) {
            totalCaloriesConsumed = 0f
            fetchUserMaintenanceCalories()
        }
    }
}
