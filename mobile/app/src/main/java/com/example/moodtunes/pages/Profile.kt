package com.example.moodtunes.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.components.MoodTunesTextField
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.PageSelected
import com.example.moodtunes.components.MoodTunesButtonField

@Composable
fun ProfilePage(navController: NavController) {
    Scaffold (
        bottomBar = { BottomBar(navController, PageSelected.Profile) },
        content = { innerPadding ->
            var actualUsername by remember { mutableStateOf("actualUsername") }
            var email by remember { mutableStateOf("email") }
            var password by remember { mutableStateOf("password") }
            var confirmPassword by remember { mutableStateOf("confirmPassword") }

            Background {
                Column(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MoodTunesTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = actualUsername,
                        onTextChange = { newText -> actualUsername = newText },
                        outlineColor = Color(0xFF5B21B6),
                        placeholder = "Username",
                        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
                        fillColor = Color.Transparent,
                        textColor = Color.White,
                        trailingIcon = Icons.Default.Edit
                    )
                    MoodTunesTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = email,
                        onTextChange = { newText -> email = newText },
                        outlineColor = Color(0xFF5B21B6),
                        placeholder = "Email",
                        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.1f),
                        fillColor = Color.Transparent,
                        textColor = Color.White,
                        trailingIcon = Icons.Default.Edit
                    )

                    PasswordForm(
                        confirmedRequired = true,
                        passwordText = password,
                        onPasswordChange = { newText -> password = newText },
                        confirmPasswordText = confirmPassword,
                        onConfirmPasswordChange = { newText -> confirmPassword = newText }
                    )
                    MoodTunesButtonField(
                        onClick = {
                            navController.navigate("select-mood")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = Color(0xFF5B21B6),
                        disabledContainerColor = Color(0x775B21B6),
                        content = {
                            Text(
                                text = "Confirm",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    )
                    MoodTunesButtonField(
                        onClick = {
                            navController.navigate("login")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = Color(0xFFB62121),
                        disabledContainerColor = Color(0x77B62121),
                        content = {
                            Text(
                                text = "Logout",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    )

                }
            }
        }
    )
}
