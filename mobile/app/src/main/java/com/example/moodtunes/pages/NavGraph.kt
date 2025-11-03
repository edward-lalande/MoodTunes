package com.example.moodtunes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moodtunes.pages.Result
import com.example.moodtunes.pages.SelectKindOfMusic
import com.example.moodtunes.pages.SelectMoodPages

@Composable
fun MoodNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "selectMood"
    ) {
        composable("selectMood") {
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