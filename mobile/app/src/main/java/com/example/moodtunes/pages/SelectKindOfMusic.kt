package com.example.moodtunes.pages

import MOOD_ICONS
import MOOD_NAME_TO_MOOD_OBJ
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.R
import com.example.moodtunes.components.TopBar
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.MoodTunesButtonField

sealed class OptionIcon {
    data class Vector(val imageVector: ImageVector) : OptionIcon()
    data class Painter(val painter: androidx.compose.ui.graphics.painter.Painter) : OptionIcon()
}

data class ContentOption(
    val title: String,
    val subtitle: String,
    val icon: OptionIcon
)

@Composable
fun ContentTypeSelector(selectedOption: String, onOptionSelected: (String) -> Unit) {
    val options = listOf(
        ContentOption("Album",
            "Discover full albums that match your mood",
            OptionIcon.Painter(painterResource(id = R.drawable.kind_of_music_album)
            )
        ),
        ContentOption("Playlists",
            "Curated playlist for your current vibes",
            OptionIcon.Painter(painterResource(id = R.drawable.kind_of_music_playlist)
            )
        ),
        ContentOption("Track",
            "Individual songs that capture your mood",
            OptionIcon.Painter(painterResource(id = R.drawable.kind_of_music_music)
            )
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            val isSelected = selectedOption == option.title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        color = if (isSelected) Color(0xFF1F1B2E) else Color(0xFF111111),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onOptionSelected(option.title) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (val icon = option.icon) {
                    is OptionIcon.Vector -> Icon(
                        imageVector = icon.imageVector,
                        contentDescription = option.title,
                        tint = Color(0xFF8E2DE2),
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0Xff9c58f7)),
                    )
                    is OptionIcon.Painter -> Icon(
                        painter = icon.painter,
                        contentDescription = option.title,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xff9c58f7)),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.title,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = option.subtitle,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                RadioButton(
                    selected = isSelected,
                    onClick = { onOptionSelected(option.title) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF8E2DE2),
                        unselectedColor = Color.Gray
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectKindOfMusic(navController: NavController, moodName: String) {
    var selectedOption by remember { mutableStateOf("Album") }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Select Content Type",
                backRoute = "select-mood",
                backDescription = "Back"
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
    ) { innerPadding ->
        Background {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "What type of content would you like for this mood?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                ContentTypeSelector(
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                MoodTunesButtonField(
                    onClick = {
                        if (selectedOption != "") {
                            println("[MoodTunes] : GO send results/$selectedOption/$moodName")
                            navController.navigate("results/$selectedOption/$moodName")
                        }
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .width(200.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF8E2DE2), Color(0xFFDA22FF))
                            ),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    backgroundColor = Color.Transparent,
                ) {
                    Text("Continue", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}
