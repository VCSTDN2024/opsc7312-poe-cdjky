package com.example.fitproplus_final

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.adapters.LeaderboardAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private val leaderboardList = mutableListOf<UserRanking>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Initialize RecyclerView and back button
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        backButton = findViewById(R.id.backButton)

        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load leaderboard data
        loadLeaderboard()

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Go back to the previous activity
        }
    }

    private fun loadLeaderboard() {
        // Fetch users and their workout counts, sorted by workout counts
        db.collection("users")
            .orderBy("workoutCount", Query.Direction.DESCENDING) // Ensure workoutCount exists in users document
            .get()
            .addOnSuccessListener { documents ->
                leaderboardList.clear()
                for (document in documents) {
                    val username = document.getString("username") ?: "Unknown"
                    val workoutCount = document.getLong("workoutCount")?.toInt() ?: 0
                    leaderboardList.add(UserRanking(username, workoutCount))
                }

                // Update the RecyclerView adapter
                leaderboardRecyclerView.adapter = LeaderboardAdapter(leaderboardList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show()
            }
    }
}
