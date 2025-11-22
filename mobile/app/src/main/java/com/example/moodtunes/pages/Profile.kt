package com.example.moodtunes.pages

import Call
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.DataObject.ErrorResponse
import com.example.moodtunes.DataObject.GetUserResponse
import com.example.moodtunes.DataObject.SignupRequest
import com.example.moodtunes.DataObject.TokenResponse
import com.example.moodtunes.components.MoodTunesTextField
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.PageSelected
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.storage.JWTHandler
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(navController: NavController) {
    Scaffold (
        bottomBar = { BottomBar(navController, PageSelected.Profile) },
        content = { innerPadding ->
            val context = LocalContext.current

            val api = remember { Call("http://10.0.2.2:8080/") }
            val scope = rememberCoroutineScope()

            var actualUsername by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("email") }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }

            val handler = JWTHandler()
            val token = handler.getToken(context)

            LaunchedEffect(token) {
                try {
                    if (token != null) {
                        val response = api.getProtected<GetUserResponse>("/user", token)
                        actualUsername = response?.username ?: "default"
                        email = response?.email ?: "default"
                        println("User data: $response")
                    } else {
                        println("No token found")
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }

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
                            scope.launch {
                                try {
                                    val request = SignupRequest(
                                        email = email,
                                        username = actualUsername,
                                        password = password
                                    )

                                    val response = api.patchProtected<ErrorResponse>("/user", token?:"", request)

                                    if (response?.error.isNullOrBlank()) {
                                        Toast.makeText(context, "Account information updated", Toast.LENGTH_LONG).show()
                                        navController.navigate("select-mood")
                                    } else {
                                        Toast.makeText(context, "Failed to update account's information", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    println("Profile info update failed: $e")
                                }
                            }
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
                            scope.launch {
                                val request = TokenResponse(
                                    token = token?:""
                                )

                                val response = api.post<ErrorResponse>("/user/logout", request)

                                if (response?.error.isNullOrBlank()) {
                                    handler.clearToken(context)
                                    navController.navigate("login")
                                } else {
                                    Toast.makeText(context, "Logout failed", Toast.LENGTH_LONG).show()
                                    println("Logout failed")
                                }
                            }
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

                    Row (
                        modifier = Modifier.padding(top = 120.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "Already want to leave us ?",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Delete my account",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable {
                                    scope.launch {
                                        try {
                                            val response = api.deleteProtected<ErrorResponse>("/user/logout", token?:"")

                                            if (response?.error.isNullOrBlank()) {
                                                Toast.makeText(context, "Account deleted", Toast.LENGTH_LONG).show()
                                                navController.navigate("sign-up")
                                            } else {
                                                Toast.makeText(context, "Failed to delete account", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            println("Profile deletion failed: $e")
                                        }
                                    }
                                }
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    )
}
