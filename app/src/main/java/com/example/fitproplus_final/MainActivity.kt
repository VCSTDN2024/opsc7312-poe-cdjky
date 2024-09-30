package com.example.fitproplus_final

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.example.fitproplus_final.LoginActivity
import com.example.fitproplus_final.R
import com.example.fitproplus_final.SignUpActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // List of motivational quotes
    private val quotes = listOf(
        "Push yourself, because no one else is going to do it for you.",
        "Success doesn’t just find you. You have to go out and get it.",
        "Don’t stop when you’re tired. Stop when you’re done.",
        "Dream it. Wish it. Do it.",
        "Wake up with determination. Go to bed with satisfaction.",
        "Do something today that your future self will thank you for."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set a random quote to the TextView
        val quoteTextView: TextView = findViewById(R.id.quoteTextView)
        val randomQuote = getRandomQuote()
        quoteTextView.text = randomQuote

        // "Let's Go" button - leads to SignUpActivity
        val letsGoButton: Button = findViewById(R.id.letsGoButton)
        letsGoButton.setOnClickListener {
            val signUpIntent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        // Handle the "Already have an account? Sign In" text
        val alreadyHaveAccountTextView: TextView = findViewById(R.id.tvAlreadyHaveAccount)

        // Full text for the TextView
        val fullText = "Already have an account? Sign In"

        // Create a SpannableString from the full text
        val spannableString = SpannableString(fullText)

        // Color the "Sign In" part and make it clickable
        val signInStartIndex = fullText.indexOf("Sign In")
        val signInEndIndex = signInStartIndex + "Sign In".length

        // Set the color of the "Sign In" part
        spannableString[signInStartIndex..signInEndIndex] = ForegroundColorSpan(Color.BLUE)

        // Create the clickable span for the "Sign In" text
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to LoginActivity
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }

        // Apply the clickable span to the "Sign In" text
        spannableString.setSpan(clickableSpan, signInStartIndex, signInEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the styled text in the TextView
        alreadyHaveAccountTextView.text = spannableString
        alreadyHaveAccountTextView.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    // Function to get a random quote
    private fun getRandomQuote(): String {
        val randomIndex = Random.nextInt(quotes.size)
        return quotes[randomIndex]
    }
}
