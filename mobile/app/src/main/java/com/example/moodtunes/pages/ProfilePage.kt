package com.example.moodtunes.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.moodtunes.components.BottomBar

@Composable
fun ProfilePage(navController: NavController) {
    Scaffold (
        content = { innerPadding ->
            Text("ProfilePage",
                modifier = Modifier.padding(innerPadding),
                color = Color.White
            )
        },
        bottomBar = { BottomBar(navController) }
    )
}
