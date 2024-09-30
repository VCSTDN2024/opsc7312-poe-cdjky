package com.example.fitproplus_final

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitproplus_final.adapters.FriendsAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var leaderboardsButton: MaterialButton
    private lateinit var trophiesButton: MaterialButton
    private lateinit var backButton: ImageButton
    private val friendsList = mutableListOf<String>() // Stores usernames of friends
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        // Initialize views
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView)
        leaderboardsButton = findViewById(R.id.leaderboardsButton)
        trophiesButton = findViewById(R.id.trophiesButton)
        backButton = findViewById(R.id.backButton)

        // Set up RecyclerView
        friendsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load friends from Firebase
        loadFriends()

        // Navigate to leaderboards
        leaderboardsButton.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

            // Navigate to My Trophies
        trophiesButton.setOnClickListener {
            startActivity(Intent(this, TrophiesActivity::class.java))
        }
    }

    // Load all users from the "users" collection in Firestore
    private fun loadFriends() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                friendsList.clear() // Clear any existing data
                for (document in documents) {
                    val username = document.getString("username")
                    if (username != null) {
                        friendsList.add(username)
                    }
                }
                // Set up the adapter for RecyclerView
                friendsRecyclerView.adapter = FriendsAdapter(friendsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load friends", Toast.LENGTH_SHORT).show()
            }
    }
}
