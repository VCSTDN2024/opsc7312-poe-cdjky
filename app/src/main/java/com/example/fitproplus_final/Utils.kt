package com.example.fitproplus_final

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Utils : AppCompatActivity() {
    companion object {
        fun applySavedLanguage(context: Context) {
            val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val languageCode = sharedPreferences.getString("selected_language", "en") ?: "en"
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}
