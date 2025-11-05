package com.example.moodtunes.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.moodtunes.components.Background

@Composable
fun SignUpPage(navController: NavController) {
    Background {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginSignUpHeader("Get started with your account!")

            SignInAndUpForm(
                navController,
                buttonText = "Sign Up",
                signUp = true
            )

            HasAnAccountForm(
                textSentence = "Already have an account?",
                textClickable = "Sign In",
                redirect = "login",
                navController = navController
            )
        }
    }
}
