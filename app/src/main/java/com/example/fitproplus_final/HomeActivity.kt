package com.example.fitproplus_final

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var lineChart: LineChart
    private lateinit var workoutPlanButton: MaterialButton
    private lateinit var manageCaloriesButton: MaterialButton
    private lateinit var friendsButton: Button
    private lateinit var profileImageView: ImageView
    private var maintenanceCalories: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        loadSavedLanguage() // Load saved language before anything else
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        welcomeTextView = findViewById(R.id.welcomeTextView)
        lineChart = findViewById(R.id.lineChart)
        workoutPlanButton = findViewById(R.id.workoutPlanButton)
        manageCaloriesButton = findViewById(R.id.manageCaloriesButton)
        friendsButton = findViewById(R.id.friendsButton)
        profileImageView = findViewById(R.id.profileImageView)

        // Retrieve the user's name and display it
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            fetchUserData(userId)
            fetchGraphData(userId)
        }

        profileImageView.setOnClickListener {
            showProfileSettings() // Show settings when profile picture is clicked
        }

        friendsButton.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        workoutPlanButton.setOnClickListener {
            startActivity(Intent(this, WorkoutActivity::class.java))
        }

        manageCaloriesButton.setOnClickListener {
            startActivity(Intent(this, CalorieIntakeActivity::class.java))
        }
    }

    private fun fetchUserData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    maintenanceCalories = document.getDouble("maintenanceCalories")?.toFloat() ?: 0f
                    welcomeTextView.text = "Welcome back, $username!"
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error fetching user data", e)
            }
    }


private fun loadSavedLanguage() {
    val sharedPreferences = getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
    val savedLanguage = sharedPreferences.getString("selectedLanguage", "en")
    val locale = Locale(savedLanguage!!)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}
    private fun fetchGraphData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val entries = mutableListOf<Entry>()
        val dateList = mutableListOf<String>()

        db.collection("users").document(userId).collection("mealLogs").get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    var i = 0
                    for (document in querySnapshot.documents) {
                        val calories = document.getDouble("calories")?.toFloat() ?: 0f
                        val timestamp = document.getLong("timestamp") ?: 0L

                        val date = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
                        dateList.add(date)
                        entries.add(Entry(i.toFloat(), calories))
                        i++
                    }
                    updateGraph(entries, dateList)
                } else {
                    lineChart.setNoDataText("No calorie data available")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error fetching graph data", e)
                Toast.makeText(this, "Error loading graph data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGraph(entries: List<Entry>, dateList: List<String>) {
        val dataSet = LineDataSet(entries, "Calories Eaten")
        dataSet.color = resources.getColor(R.color.primary)
        dataSet.valueTextColor = resources.getColor(R.color.black)
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(resources.getColor(R.color.primary))
        dataSet.lineWidth = 3f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = resources.getDrawable(R.drawable.graph_fill)

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Add a red line to represent the maintenance calories
        val maintenanceLine = LimitLine(maintenanceCalories, "Maintenance Calories")
        maintenanceLine.lineColor = resources.getColor(R.color.red)
        maintenanceLine.lineWidth = 2f
        maintenanceLine.textColor = resources.getColor(R.color.black)
        maintenanceLine.textSize = 12f

        val leftAxis = lineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(maintenanceLine)
        leftAxis.granularity = 200f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 3000f
        leftAxis.setLabelCount(15, true)

        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dateList.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.invalidate() // Refresh the chart with updated data
    }

    private fun showProfileSettings() {
        // Inflate the bottom sheet view
        val bottomSheetView = layoutInflater.inflate(R.layout.layout_profile_settings, null)

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Handle clicks on the settings options
        bottomSheetView.findViewById<TextView>(R.id.editProfileTextView).setOnClickListener {
            // Navigate to edit profile activity
            startActivity(Intent(this, EditProfileActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.changeDailyCaloriesTextView).setOnClickListener {
            // Navigate to change daily calories activity
            startActivity(Intent(this, ChangeMaintenanceCaloriesActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.changePersonalDetailsTextView).setOnClickListener {
            // Navigate to change personal details activity
            startActivity(Intent(this, ChangePersonalDetailsActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.changeLanguageTextView).setOnClickListener {
            // Navigate to change language activity
            startActivity(Intent(this, ChangeLanguageActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.changePasswordTextView).setOnClickListener {
            // Navigate to change password activity
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.logoutTextView).setOnClickListener {
            // Perform logout
            logout()
            bottomSheetDialog.dismiss()
        }

        // Show the dialog
        bottomSheetDialog.show()
    }

    private fun logout() {
        // Handle logout logic (e.g., FirebaseAuth.getInstance().signOut())
        FirebaseAuth.getInstance().signOut()
        // Redirect to login activity after logout
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }



}
