package com.example.moodtunes.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.PageSelected

@Composable
fun ProfilePage(navController: NavController) {
    Scaffold (
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF312B90),
                        Color.Black,
                        Color(0xFF312B90)
                    ),
                )
            ),
        content = { innerPadding ->
            Text("ProfilePage",
                modifier = Modifier.padding(innerPadding),
                color = Color.White
            )
        },
        bottomBar = { BottomBar(navController, PageSelected.Profile) }
    )
}
