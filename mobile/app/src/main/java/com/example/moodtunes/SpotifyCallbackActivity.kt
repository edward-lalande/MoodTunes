package com.example.moodtunes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import api
import com.example.moodtunes.DataObject.SpotifyCallbackResponse
import com.example.moodtunes.storage.JWTHandler
import kotlinx.coroutines.launch

class SpotifyCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        if (uri != null) {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")

            if (code != null && state != null) {
                handleSpotifyCallback(code, state)
            } else {
                println("SpotifyCallbackActivity - Missing code or state")
                navigateToMain()
            }
        } else {
            println("No URI")
            navigateToMain()
        }
    }

    private fun handleSpotifyCallback(code: String, state: String) {
        lifecycleScope.launch {
            try {
                val encodedCode = java.net.URLEncoder.encode(code, "UTF-8")
                val encodedState = java.net.URLEncoder.encode(state, "UTF-8")
                val url = "/spotify/callback?code=$encodedCode&state=$encodedState"

                val response = api.get<SpotifyCallbackResponse>(url)

                if (response?.token != null && response.token.isNotBlank()) {
                    val handler = JWTHandler()
                    handler.saveToken(this@SpotifyCallbackActivity, response.token)

                    Toast.makeText(
                        this@SpotifyCallbackActivity,
                        "Spotify connected successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this@SpotifyCallbackActivity, MainActivity::class.java)
                    intent.putExtra("navigate_to", "select-mood")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    navigateToMain()
                }
            } catch (e: Exception) {
                println("Oauth callback failed: $e")
                navigateToMain()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}