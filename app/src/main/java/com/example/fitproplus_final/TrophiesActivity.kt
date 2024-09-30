package com.example.fitproplus_final

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.adapters.TrophiesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TrophiesActivity : AppCompatActivity() {

    private lateinit var trophiesRecyclerView: RecyclerView
    private val trophiesList = mutableListOf<String>() // Placeholder for trophies data
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trophies)

        // Initialize RecyclerView
        trophiesRecyclerView = findViewById(R.id.trophiesRecyclerView)
        trophiesRecyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)

        // Load trophies
        loadTrophies()

        backButton.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }
    }


    private fun loadTrophies() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("workouts")
            .get()
            .addOnSuccessListener { documents ->
                val workoutCount = documents.size()
                generateTrophies(workoutCount)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load workouts", Toast.LENGTH_SHORT).show()
            }
    }


    // Generate trophies based on workout count or other criteria
    private fun generateTrophies(workoutCount: Int) {
        trophiesList.clear() // Clear any existing trophies

        if (workoutCount >= 1) {
            trophiesList.add("Beginner: Logged 1 Workout")
        }
        if (workoutCount >= 5) {
            trophiesList.add("Fitness Enthusiast: Logged 5 Workouts")
        }
        if (workoutCount >= 10) {
            trophiesList.add("Pro Athlete: Logged 10 Workouts")
        }
        if (workoutCount >= 20) {
            trophiesList.add("Fitness Legend: Logged 20 Workouts")
        }

        // Refresh the adapter to display trophies
        trophiesRecyclerView.adapter = TrophiesAdapter(trophiesList)
    }
}
