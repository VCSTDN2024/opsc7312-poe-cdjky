package com.example.fitproplus_final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitproplus_final.models.WorkoutLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LogWorkoutActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var enterButton: Button
    private lateinit var exerciseNameEditText: EditText
    private lateinit var setsEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var durationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_workout)

        backButton = findViewById(R.id.backButton)
        enterButton = findViewById(R.id.enterButton)
        exerciseNameEditText = findViewById(R.id.exerciseNameEditText)
        setsEditText = findViewById(R.id.setsEditText)
        repsEditText = findViewById(R.id.repsEditText)
        durationEditText = findViewById(R.id.durationEditText)

        // Back button click handler
        backButton.setOnClickListener {
            finish()
        }

        // Log workout button click handler
        enterButton.setOnClickListener {
            logWorkout()
        }
    }

    private fun logWorkout() {
        // Retrieve values from input fields
        val exerciseName = exerciseNameEditText.text.toString().trim()
        val sets = setsEditText.text.toString().toIntOrNull()
        val reps = repsEditText.text.toString().toIntOrNull()
        val duration = durationEditText.text.toString().toIntOrNull()

        // Validation for input fields
        if (exerciseName.isEmpty() || sets == null || reps == null || duration == null) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the user's ID from FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Create the WorkoutLog object with input values
            val workout = WorkoutLog(exerciseName, sets, reps, duration, System.currentTimeMillis())

            val firestore = FirebaseFirestore.getInstance()
            val userRef = firestore.collection("users").document(userId)

            // Add the workout to the user's collection
            userRef.collection("workouts").add(workout)
                .addOnSuccessListener {
                    Toast.makeText(this, "Workout logged successfully", Toast.LENGTH_SHORT).show()

                    // Increment workoutCount in Firestore
                    userRef.update("workoutCount", FieldValue.increment(1))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Workout count updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update workout count", Toast.LENGTH_SHORT).show()
                        }

                    // Pass information back to WorkoutActivity that the workout was logged
                    val intent = Intent()
                    intent.putExtra("workoutLogged", true) // Inform that a workout was logged
                    setResult(Activity.RESULT_OK, intent)

                    finish() // Close this activity and return to the WorkoutActivity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log workout", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
