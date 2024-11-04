package com.example.fitproplus_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView
    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get references to UI elements
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
        languageSpinner = findViewById(R.id.languageSpinner)

        // Language selection Spinner setup
        // Language selection Spinner setup
        // Declare a variable to hold the currently selected language
        // Declare a variable to hold the currently selected language
        var currentLanguage: String = "en"
        var isLocaleChanged = false // Flag to prevent repeated recreation

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = when (position) {
                    1 -> "af"  // Afrikaans
                    2 -> "zu"  // Zulu
                    else -> "en"  // Default to English
                }

                // Check if the selected language differs and avoid repeated recreation
                if (selectedLanguage != currentLanguage && !isLocaleChanged) {
                    currentLanguage = selectedLanguage
                    saveLanguagePreference(selectedLanguage)
                    isLocaleChanged = true  // Set flag to prevent repeated recreation
                    setLocale(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        fun onResume() {
            super.onResume()
            // Reset the flag when activity restarts
            isLocaleChanged = false
        }




        // Login button click listener
        loginButton.setOnClickListener {
            val emailOrUsername = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                if (emailOrUsername.contains("@")) {
                    signInWithEmail(emailOrUsername, password)
                } else {
                    signInWithUsername(emailOrUsername, password)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to signup page if user clicks on "Don't have an account? Sign up here"
        signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // Save the selected language in SharedPreferences
    private fun saveLanguagePreference(languageCode: String) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("selected_language", languageCode).apply()
    }

    // Set locale based on the selected language and refresh the activity
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // Refresh the activity to apply the language change
    }

    // Sign in with email and password using Firebase Auth
    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHomeActivity()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Sign in with username and password
    private fun signInWithUsername(username: String, password: String) {
        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val email = documents.documents[0].getString("email")
                    if (email != null) {
                        signInWithEmail(email, password)
                    } else {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to navigate to home screen after successful login
    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
