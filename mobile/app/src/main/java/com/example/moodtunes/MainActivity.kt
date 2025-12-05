package com.example.moodtunes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.moodtunes.components.Background
import com.example.moodtunes.pages.MoodNavGraph
import com.example.moodtunes.ui.theme.MoodTunesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val navigateTo = intent.getStringExtra("navigate_to")

        setContent {
            MoodTunesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Black
                ) { innerPadding ->
                    Background {
                        MoodTunesApp(
                            modifier = Modifier.padding(innerPadding),
                            startDestination = navigateTo
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoodTunesApp(modifier: Modifier = Modifier, startDestination: String? = null) {
    val navController = rememberNavController()

    LaunchedEffect(startDestination) {
        if (startDestination != null) {
            navController.navigate(startDestination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    MoodNavGraph(navController)
}

@Preview(showBackground = true)
@Composable
fun PreviewMoodCard() {
    MoodTunesTheme {
        MoodTunesApp()
    }
}