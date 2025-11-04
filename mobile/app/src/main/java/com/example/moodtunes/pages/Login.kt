package com.example.moodtunes.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.R
import com.example.moodtunes.components.MoodTunesTextField
import com.example.moodtunes.components.MoodTunesButtonField

@Composable
fun LoginPage(navController: NavController) {
    LoginBackground {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader()

            SpotifyButton()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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

            SignInForm(navController)

            SignUpForm()
        }
    }
}

@Composable
fun LoginHeader() {
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
        text = "Music that matches your mood",
        color = Color.Gray,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun SpotifyButton() {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1DB954),
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
fun SignInForm(navController: NavController) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

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

    MoodTunesTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        text = passwordText,
        onTextChange = { newText -> passwordText = newText },
        outlineColor = Color(0xFF5B21B6),
        placeholder = "Password",
        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
        fillColor = Color.Transparent,
        textColor = Color.White
    )

    MoodTunesButtonField(
        onClick = {
            navController.navigate("select-mood")
        },
        text = "Sign In",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color(0xFF5B21B6),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
    )
}

@Composable
fun SignUpForm() {
    Row {
        Text(
            text = "Don't have an account?",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Text(
            text = "Sign up",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable { }
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun LoginBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF312B90),
                        Color.Black,
                        Color(0xFF312B90)
                    ),
                )
            )
    ) {
        content()
    }
}