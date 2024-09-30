package com.example.fitproplus_final

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeMaintenanceCaloriesActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var maintenanceCaloriesEditText: EditText
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_maintenance_calories)

        // Initialize the UI components
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        maintenanceCaloriesEditText = findViewById(R.id.maintenanceCaloriesEditText)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }

        // Save button functionality
        saveButton.setOnClickListener {
            val maintenanceCalories = maintenanceCaloriesEditText.text.toString().trim()

            // Check if the input is valid
            if (maintenanceCalories.isEmpty()) {
                Toast.makeText(this, "Please enter your maintenance calories", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update maintenance calories in Firestore
            updateMaintenanceCalories(maintenanceCalories.toFloat())
        }
    }

    // Function to update maintenance calories in Firestore
    private fun updateMaintenanceCalories(maintenanceCalories: Float) {
        val userId = auth.currentUser?.uid

        // Ensure the user is logged in
        if (userId != null) {
            val userRef = firestore.collection("users").document(userId)

            // Update the maintenance calories field in Firestore
            userRef.update("maintenanceCalories", maintenanceCalories)
                .addOnSuccessListener {
                    Toast.makeText(this, "Maintenance calories updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Return to the previous activity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update maintenance calories", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
