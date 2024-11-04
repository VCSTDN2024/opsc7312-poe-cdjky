package com.example.fitproplus_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
        languageSpinner = findViewById(R.id.languageSpinner)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLanguage = when (position) {
                    1 -> "af"
                    2 -> "zu"
                    else -> "en"
                }
                //saveLanguagePreference(selectedLanguage) // Comment out for debugging
                //setLocale(selectedLanguage) // Comment out for debugging
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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

        signupText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHomeActivity()
                } else {
                    Log.e("LoginActivity", "Login failed: ${task.exception?.message}")
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

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
                Log.e("LoginActivity", "Error fetching user data: ${e.message}")
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
