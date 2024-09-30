package com.example.fitproplus_final

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize the UI components
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }

        // Prepopulate the fields with existing user data
        loadUserData()

        // Save button functionality
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()

            // Check if the input is valid
            if (name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update user details in Firestore
            updateUserProfile(name, surname)
        }
    }

    // Function to load current user data and prepopulate the fields
    private fun loadUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        nameEditText.setText(name)
                        surnameEditText.setText(surname)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to update user details in Firestore
    private fun updateUserProfile(name: String, surname: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = firestore.collection("users").document(userId)

            // Update the user's name and surname in Firestore
            userRef.update("name", name, "surname", surname)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Return to the previous activity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
