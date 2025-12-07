package com.example.moodtunes.pages

import MOOD_ICONS
import MOOD_NAME_TO_MOOD_OBJ
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.PageSelected
import com.example.moodtunes.components.TopBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.moodtunes.R
import androidx.core.net.toUri
import api
import coil.compose.AsyncImage
import com.example.moodtunes.DataObject.MusicDetailed
import com.example.moodtunes.DataObject.MusicPlaylistResponse
import com.example.moodtunes.DataObject.NormalMoodRequest
import com.example.moodtunes.storage.JWTHandler
import com.google.gson.Gson

@Composable
fun Result(navController: NavHostController, selectedOption: String, moodName: String) {
    val context = LocalContext.current
    var apiResp by remember { mutableStateOf<MusicPlaylistResponse?>(null) }
    var resp by remember { mutableStateOf<MusicDetailed?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val token = JWTHandler().getToken(context)
            println("token: $token")
            apiResp = api.request<MusicPlaylistResponse?>(
                method = "POST",
                url = "http://10.0.2.2:8080/music/mood",
                jsonBody = Gson().toJson(NormalMoodRequest(mood = moodName, kind = selectedOption)),
                token
            )
            println("apiResp: ${apiResp.toString()}")
            if (apiResp != null) {
                isLoading = false
            }
            resp = apiResp?.playlist[0]
        } catch(e: Error) {
            println("Error: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Mood Result",
                backRoute = "select-mood",
                backDescription = "Back"
            )
        },
        content = { innerPadding ->
            Background {
                if (isLoading && resp == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .padding(top = 100.dp)
                        )
                    }
                } else {
                    Column (
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MoodTunesButtonField(
                            onClick = {},
                            modifier = Modifier
                                .padding(top = 22.dp, bottom = 14.dp)
                                .height(48.dp)
                                .width(148.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF8E2DE2), Color(0xFFDA22FF))
                                    ),
                                    shape = RoundedCornerShape(100.dp)
                                ),
                            content = {
                                Text(
                                    text = "${MOOD_ICONS[MOOD_NAME_TO_MOOD_OBJ[moodName]]} $moodName",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        background = Color.Transparent
                                    )
                                )
                            }
                        )
                        Text(
                            text = "$selectedOption found for '$moodName'",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                background = Color.Transparent
                            )
                        )
                        Text(
                            text = "Perfect match for your current mood",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        AsyncImage(
                            model = resp?.albumCoverUrl,
                            contentDescription = "Cover of the album",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .aspectRatio(1f)
                                .padding(top = 24.dp)
                                .width(64.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Text(
                            text = "${resp?.title}",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                background = Color.Transparent
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "by ${resp?.artist}",
                            fontSize = 19.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Released: ${resp?.releaseDate}",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        MoodTunesButtonField(
                            onClick = {
                                val viewIntent = Intent("android.intent.action.VIEW",
                                    resp?.spotifyUrl?.toUri());
                                context.startActivity(viewIntent);
                            },
                            backgroundColor = Color(0xFFDA22FF),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.spotify),
                                    contentDescription = "Spotify Icon",
                                    modifier = Modifier
                                        .size(45.dp)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = "Open in Spotify",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = { BottomBar(navController, PageSelected.Home) }
    )
}