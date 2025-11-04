package com.example.moodtunes.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentColor = Color.Black,
        containerColor = Color.Black,
    ){
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            IconButton(
                onClick = { navController.navigate("select-mood") },
                modifier = Modifier.width(16.dp),
                colors = IconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.White,
                    disabledContainerColor = Color.Blue,
                    disabledContentColor = Color.Blue
                )
            ) {
                Icons.Default.Home
            }
            IconButton(
                onClick = { navController.navigate("history") },
                modifier = Modifier.width(16.dp),
                colors = IconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.White,
                    disabledContainerColor = Color.Blue,
                    disabledContentColor = Color.Blue
                )
            ) {
                Icons.Default.Phone
            }
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.width(16.dp),
                colors = IconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.White,
                    disabledContainerColor = Color.Blue,
                    disabledContentColor = Color.Blue
                )
            ) {
                Icons.Default.Person
            }
        }
    }
}
