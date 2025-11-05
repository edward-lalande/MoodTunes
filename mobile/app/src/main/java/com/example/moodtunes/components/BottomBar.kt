package com.example.moodtunes.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moodtunes.R

enum class PageSelected {
    Home,
    History,
    Profile,
}

@Composable
fun BottomBar(navController: NavController, page: PageSelected) {
    BottomAppBar (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentColor = Color.Black,
        containerColor = Color.Black,
    ){
        Row (
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            IconButton(
                onClick = { navController.navigate("history") },
                modifier = Modifier.width(34.dp).height(34.dp),
                colors = IconButtonColors(
                    contentColor = if (page == PageSelected.History) Color(0xFF7E6BBA) else Color.White,
                    containerColor = Color.Black,
                    disabledContentColor = if (page == PageSelected.History) Color(0xFF7E6BBA) else Color.White,
                    disabledContainerColor = Color.Black
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.history_line_icon),
                    contentDescription = "history",
                    modifier = Modifier.width(34.dp).height(29.dp)
                )
            }
            IconButton(
                onClick = { navController.navigate("select-mood") },
                modifier = Modifier.width(34.dp).height(34.dp),
                colors = IconButtonColors(
                    contentColor = if (page == PageSelected.Home) Color(0xFF7E6BBA) else Color.White,
                    containerColor = Color.Black,
                    disabledContentColor = if (page == PageSelected.Home) Color(0xFF7E6BBA) else Color.White,
                    disabledContainerColor = Color.Black
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.width(34.dp).height(34.dp)
                )
            }
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.width(34.dp).height(34.dp),
                colors = IconButtonColors(
                    contentColor = if (page == PageSelected.Profile) Color(0xFF7E6BBA) else Color.White,
                    containerColor = Color.Black,
                    disabledContentColor = if (page == PageSelected.Profile) Color(0xFF7E6BBA) else Color.White,
                    disabledContainerColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "profile",
                    modifier = Modifier.width(34.dp).height(34.dp)
                )
            }
        }
    }
}
