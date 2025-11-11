package com.example.moodtunes.pages

import Call
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.DataObject.UserData
import com.example.moodtunes.R
import com.example.moodtunes.components.MoodTunesTextField
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.Background
import kotlinx.coroutines.launch
import retrofit2.Response

val api = Call("http://192.168.200.176:3000/")

@Composable
fun LoginPage(navController: NavController) {
    var user by remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(Unit) {
        try {
            user = api.get<UserData>("login")
        } catch (e: Exception){
            user = null
            println(e.toString())
        }
    }

    Background {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginSignUpHeader("Music that matches your mood")

            SpotifyButton()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                HorizontalDivider(
                    Modifier.weight(1f),
                    DividerDefaults.Thickness,
                    color = Color.Gray
                )
                Text(
                    text = "or",
                    Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                HorizontalDivider(
                    Modifier.weight(1f),
                    DividerDefaults.Thickness,
                    color = Color.Gray
                )
            }

            SignInAndUpForm(
                navController,
                buttonText = "Sign in",
                signUp = false,
                userName = user?.email,
                password = user?.password
            )

            HasAnAccountForm(
                textSentence = "Don't have an account?",
                textClickable = "Sign Up",
                redirect = "sign-up",
                navController = navController
            )
        }
    }
}

@Composable
fun LoginSignUpHeader(subtitleText: String) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF5B21B6),
            contentColor = Color.White,
        ),
        modifier = Modifier.padding(20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.musicnote),
            contentDescription = "Music Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .padding(30.dp)
                .size(30.dp)
        )
    }

    Text(
        text = "MoodTunes",
        color = Color.White,
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )

    Text(
        text = subtitleText,
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun SpotifyButton() {
    val scope = rememberCoroutineScope()
    var resp by remember { mutableStateOf<Response<Unit>?>(null) }

    Button(
        onClick = {
            scope.launch {
                println("scope launched")
                try {
                    resp = api.post<Response<Unit>>("spotify", null)
                } catch (e: Exception) {
                    println("SpotifyErr: ${e.message}")
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (resp?.isSuccessful == true) Color(0xFF1DB954) else Color.Red,
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.spotify),
                contentDescription = "Spotify Icon",
                modifier = Modifier
                    .size(45.dp)
                    .padding(end = 12.dp)
            )
            Text(
                text = "Continue with Spotify",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PasswordForm(
    confirmedRequired: Boolean,
    passwordText: String,
    onPasswordChange: (String) -> Unit,
    confirmPasswordText: String,
    onConfirmPasswordChange: (String) -> Unit
) {
    MoodTunesTextField(
        isPassword = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        text = passwordText,
        onTextChange = { newText -> onPasswordChange(newText) },
        outlineColor = Color(0xFF5B21B6),
        placeholder = "Password",
        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
        fillColor = Color.Transparent,
        textColor = Color.White
    )

    if (confirmedRequired) {
        var outlineConfirmField = Color.Red

        if (confirmPasswordText == passwordText) {
            outlineConfirmField = Color(0xFF5B21B6)
        }

        MoodTunesTextField(
            isPassword = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            text = confirmPasswordText,
            onTextChange = { newText -> onConfirmPasswordChange(newText) },
            outlineColor = outlineConfirmField,
            placeholder = "Confirm Password",
            backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
            fillColor = Color.Transparent,
            textColor = Color.White
        )
    }
}

@Composable
fun SignInAndUpForm(
    navController: NavController,
    buttonText: String,
    signUp: Boolean,
    userName: String? = null,
    password: String? = null
) {
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
        confirmedRequired = signUp,
        passwordText = password ?: passwordText,
        onPasswordChange = { newText -> passwordText = newText },
        confirmPasswordText = confirmPasswordText,
        onConfirmPasswordChange = { newText -> confirmPasswordText = newText }
    )
    var isEnableButton = true
    if (passwordText != confirmPasswordText && signUp) {
        isEnableButton = false
    }

    MoodTunesButtonField(
        onClick = {
            navController.navigate("select-mood")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color(0xFF5B21B6),
        disabledContainerColor = Color(0x775B21B6),
        enabled = isEnableButton,
        content = {
            Text(
                text = buttonText,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    )
}

@Composable
fun HasAnAccountForm(navController: NavController ,textSentence: String, textClickable: String, redirect: String) {
    Row {
        Text(
            text = textSentence,
            color = Color.Gray,
            fontSize = 16.sp
        )

        Text(
            text = textClickable,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable { navController.navigate(redirect) }
                .padding(horizontal = 8.dp)
        )
    }
}
