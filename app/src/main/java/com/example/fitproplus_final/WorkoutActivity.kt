package com.example.fitproplus_final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitproplus_final.models.WorkoutLog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class WorkoutActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var backButton: ImageButton
    private lateinit var logWorkoutButton: com.google.android.material.button.MaterialButton

    private val REQUEST_LOG_WORKOUT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        lineChart = findViewById(R.id.lineChart)
        backButton = findViewById(R.id.backButton)
        logWorkoutButton = findViewById(R.id.logWorkoutButton)

        backButton.setOnClickListener {
            finish()
        }

        logWorkoutButton.setOnClickListener {
            val intent = Intent(this, LogWorkoutActivity::class.java)
            startActivityForResult(intent, REQUEST_LOG_WORKOUT)
        }

        setupGraph()
    }

    private fun setupGraph() {
        val entries = mutableListOf<Entry>()

        // Fetch logged workouts and populate graph
        fetchLoggedWorkouts { workoutEntries ->
            entries.addAll(workoutEntries)

            val dataSet = LineDataSet(entries, "Workouts Logged")
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line
            dataSet.color = resources.getColor(R.color.primary)
            dataSet.lineWidth = 2f
            dataSet.setDrawFilled(true)
            dataSet.fillColor = resources.getColor(R.color.primary)
            dataSet.setCircleColor(resources.getColor(R.color.primary))

            val lineData = LineData(dataSet)
            lineChart.data = lineData

            // Customize X-Axis to show days of the week
            val xAxis: XAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            // X-Axis should display Monday to Sunday
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                override fun getFormattedValue(value: Float): String {
                    return daysOfWeek.getOrNull(value.toInt()) ?: value.toString()
                }
            }

            // Customize Y-Axis to show the number of workouts (1-10)
            val leftAxis = lineChart.axisLeft
            leftAxis.granularity = 1f
            leftAxis.axisMinimum = 0f
            leftAxis.axisMaximum = 10f
            leftAxis.setDrawGridLines(true)

            // Disable right Y-Axis
            lineChart.axisRight.isEnabled = false

            // Refresh the chart with the updated data
            lineChart.invalidate()
        }
    }

    private fun fetchLoggedWorkouts(onFetchComplete: (List<Entry>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val weekStart = LocalDate.now(ZoneId.of("Africa/Johannesburg")).minusDays(6).atStartOfDay().toEpochSecond(ZoneOffset.UTC)

            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .whereGreaterThan("timestamp", weekStart)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val workoutEntries = mutableListOf<Entry>()

                    // Map to count workouts per day
                    val workoutCountMap = mutableMapOf<Int, Float>()
                    querySnapshot.documents.forEach { document ->
                        val workout = document.toObject(WorkoutLog::class.java)
                        workout?.let {
                            val dayOfWeek = LocalDate.ofEpochDay(it.timestamp / 86400).dayOfWeek.value // Get day number (1 = Monday, 7 = Sunday)
                            workoutCountMap[dayOfWeek] = workoutCountMap.getOrDefault(dayOfWeek, 0f) + 1f
                        }
                    }

                    // Create entries for the graph
                    for (i in 1..7) { // Days of the week
                        val count = workoutCountMap.getOrDefault(i, 0f)
                        workoutEntries.add(Entry((i - 1).toFloat(), count)) // Monday = 0, Sunday = 6
                    }

                    onFetchComplete(workoutEntries)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load workouts", Toast.LENGTH_SHORT).show()
                    onFetchComplete(emptyList()) // Return empty list in case of failure
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOG_WORKOUT && resultCode == Activity.RESULT_OK) {
            // Refresh the graph if a workout was logged
            setupGraph()
        }
    }
}
