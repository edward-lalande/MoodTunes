package com.example.moodtunes

import MoodCard
import Music
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.MoodTunesTextField
import com.example.moodtunes.ui.theme.MoodTunesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodTunesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Black
                ) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MoodCard(
          mood = Mood.Energetic,
          time = "2:30 PM",
          music = Music(
            name = "Good 4 U",
            artist = "Olivia Rodrigo",
            releaseDate = "2021",
          ),
          modifier = modifier
              .fillMaxWidth()
              .padding(16.dp)
        )
        MoodTunesTextField(
            text = searchText,
            onTextChange = { newText -> searchText = newText },
            outlineColor = Color.Gray,
            fillColor = Color(red = 6.7f / 255f, green = 12f / 255f, blue = 25f / 255f),
            placeholder = "Search Your moods",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        MoodTunesButtonField(
            text = "Search",
            onClick = {},
            backgroundColor = Color.Red,
            contentColor = Color.White,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewMoodCard() {
    MoodTunesTheme {
        Greeting()
    }
}
