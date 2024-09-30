package com.example.fitproplus_final

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ChangeLanguageActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var languageRadioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)

        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        languageRadioGroup = findViewById(R.id.languageRadioGroup)

        // Initialize shared preferences to store language selection
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }

        // Save button functionality
        saveButton.setOnClickListener {
            val selectedLanguage = when (languageRadioGroup.checkedRadioButtonId) {
                R.id.englishRadioButton -> "en"
                R.id.zuluRadioButton -> "zu"
                R.id.afrikaansRadioButton -> "af"
                else -> "en" // Default to English
            }
            changeLanguage(selectedLanguage)
        }
    }

    // Function to change the language of the app
    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        // Update the app's configuration
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("selectedLanguage", languageCode)
        editor.apply()

        // Restart the activity to apply the language change
        val refresh = Intent(this, HomeActivity::class.java)
        startActivity(refresh)
        finish()

        Toast.makeText(this, "Language changed to ${Locale(languageCode).displayLanguage}", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadSavedLanguage()
    }

    private fun loadSavedLanguage() {
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en")
        val locale = Locale(savedLanguage!!)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Set the saved language as selected
        when (savedLanguage) {
            "en" -> languageRadioGroup.check(R.id.englishRadioButton)
            "zu" -> languageRadioGroup.check(R.id.zuluRadioButton)
            "af" -> languageRadioGroup.check(R.id.afrikaansRadioButton)
        }
    }
}
