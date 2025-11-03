package com.example.moodtunes.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun Result(navController: NavHostController, selectedOption: String, moodName: String) {
    Text("Result yeah yeah $moodName for $selectedOption", color = Color.White)
}