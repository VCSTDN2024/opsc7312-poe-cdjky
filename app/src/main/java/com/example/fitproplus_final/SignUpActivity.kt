package com.example.fitproplus_final

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var maintenanceCaloriesEditText: TextInputEditText
    private lateinit var heightEditText: TextInputEditText
    private lateinit var weightEditText: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var signupButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        maintenanceCaloriesEditText = findViewById(R.id.maintenanceCaloriesEditText)
        heightEditText = findViewById(R.id.heightEditText)
        weightEditText = findViewById(R.id.weightEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        signupButton = findViewById(R.id.signupButton)

        // Set up the Signup button listener
        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val maintenanceCalories = maintenanceCaloriesEditText.text.toString().trim()
            val height = heightEditText.text.toString().trim()
            val weight = weightEditText.text.toString().trim()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val genderRadioButton = findViewById<RadioButton>(selectedGenderId)
            val gender = genderRadioButton?.text.toString()

            if (validateInput(username, email, password, maintenanceCalories, height, weight, gender)) {
                createAccount(username, email, password, maintenanceCalories, height, weight, gender)
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        maintenanceCalories: String,
        height: String,
        weight: String,
        gender: String
    ): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || maintenanceCalories.isEmpty() || height.isEmpty() || weight.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        // Ensure maintenanceCalories, height, and weight are valid numbers
        if (maintenanceCalories.toDoubleOrNull() == null || height.toDoubleOrNull() == null || weight.toDoubleOrNull() == null) {
            Toast.makeText(this, "Please enter valid numbers for calories, height, and weight", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createAccount(
        username: String,
        email: String,
        password: String,
        maintenanceCalories: String,
        height: String,
        weight: String,
        gender: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid

                // Convert the inputs to numbers before saving
                val maintenanceCaloriesValue = maintenanceCalories.toDouble()
                val heightValue = height.toDouble()
                val weightValue = weight.toDouble()

                // Save user details in Firestore
                val user = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "maintenanceCalories" to maintenanceCaloriesValue,  // Store as Double
                    "height" to heightValue,  // Store as Double
                    "weight" to weightValue,  // Store as Double
                    "gender" to gender
                )

                firestore.collection("users").document(userId!!).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                        // Redirect to Login Activity
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w("SignUpActivity", "Error adding document", e)
                        Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
