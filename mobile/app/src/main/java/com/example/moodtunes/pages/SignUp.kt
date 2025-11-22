package com.example.moodtunes.pages

import Call
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.DataObject.SignupRequest
import com.example.moodtunes.DataObject.TokenResponse
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.MoodTunesTextField
import kotlinx.coroutines.launch
import com.example.moodtunes.storage.JWTHandler


@Composable
fun SignUpPage(navController: NavController) {
    Background {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginSignUpHeader("Get started with your account!")

            SignUpForm(
                navController,
                password = null,
                userName = null
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

@Composable
fun SignUpForm(
    navController: NavController,
    userName: String? = null,
    password: String? = null
) {
    val context = LocalContext.current

    val api = remember { Call("http://10.0.2.2:8080/") }
    val scope = rememberCoroutineScope()

    var usernameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }

    MoodTunesTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = userName ?: usernameText,
        onTextChange = { newText -> usernameText = newText },
        outlineColor = Color(0xFF5B21B6),
        placeholder = "Username",
        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
        fillColor = Color.Transparent,
        textColor = Color.White
    )

    MoodTunesTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = emailText,
        onTextChange = { newText -> emailText = newText },
        outlineColor = Color(0xFF5B21B6),
        placeholder = "Email",
        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
        fillColor = Color.Transparent,
        textColor = Color.White
    )

    PasswordForm(
        confirmedRequired = true,
        passwordText = password ?: passwordText,
        onPasswordChange = { newText -> passwordText = newText },
        confirmPasswordText = confirmPasswordText,
        onConfirmPasswordChange = { newText -> confirmPasswordText = newText }
    )
    var isEnableButton = true
    if (passwordText != confirmPasswordText) {
        isEnableButton = false
    }

    MoodTunesButtonField(
        onClick = {
            scope.launch {
                try {
                    val request = SignupRequest(
                        email = emailText,
                        password = passwordText,
                        username = usernameText
                    )

                    val response = api.post<TokenResponse>("/user/create", request)

                    if (!response?.token.isNullOrBlank()) {
                        val handler = JWTHandler()
                        handler.saveToken(context, response.token)

                        navController.navigate("select-mood")
                    } else {
                        Toast.makeText(context, "Sign up failed", Toast.LENGTH_LONG).show()
                        println("Sign Up failed")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(e.message)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color(0xFF5B21B6),
        disabledContainerColor = Color(0x775B21B6),
        enabled = isEnableButton,
        content = {
            Text(
                text = "Sign Up",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    )
}
