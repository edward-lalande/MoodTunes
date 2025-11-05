package com.example.moodtunes.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun MoodNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginPage(navController = navController)
        }
        composable("sign-up") {
            SignUpPage(navController = navController)
        }
        composable("select-mood") {
            SelectMoodPages(navController = navController)
        }
        composable(
            "select-kind-of-music/{moodName}",
            arguments = listOf(navArgument("moodName") { type = NavType.StringType })
        ) { backStackEntry ->
            val moodName = backStackEntry.arguments?.getString("moodName") ?: ""
            SelectKindOfMusic(navController, moodName)
        }
        composable(
            "history",
        ) {
            HistoryPage(navController)
        }
        composable(
            "profile",
        ) {
            ProfilePage(navController)
        }
        composable(
            "results/{selectedOption}/{moodName}",
            arguments = listOf(
                navArgument("selectedOption") { type = NavType.StringType },
                navArgument("moodName") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val selectedOption = backStackEntry.arguments?.getString("selectedOption") ?: ""
            val moodName = backStackEntry.arguments?.getString("moodName") ?: ""
            Result(navController, selectedOption, moodName)
        }
    }
}