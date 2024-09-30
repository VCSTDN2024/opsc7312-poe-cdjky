package com.example.fitproplus_final

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ChangePersonalDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_personal_details)

        // Initialize views
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Return to the previous activity
        }

        // Save button functionality to update personal details
        saveButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newEmail = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                updateUserDetails(newUsername, newEmail, password)
            }
        }

        // Load current user details into the fields
        loadUserDetails()
    }

    // Function to load current username and email into EditText fields
    private fun loadUserDetails() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: ""
                        usernameEditText.setText(username)
                        emailEditText.setText(user.email)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to update the user's username and email address
    private fun updateUserDetails(newUsername: String, newEmail: String, password: String) {
        val user = auth.currentUser
        if (user != null) {
            // Re-authenticate the user before changing email
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Update email in Firebase Auth
                    user.updateEmail(newEmail)
                        .addOnSuccessListener {
                            // Update Firestore username
                            firestore.collection("users").document(user.uid).update("username", newUsername)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update email: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Authentication failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
