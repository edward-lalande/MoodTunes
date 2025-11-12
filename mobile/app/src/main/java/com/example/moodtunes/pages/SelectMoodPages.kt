package com.example.moodtunes.pages

import MOOD_ICONS
import Mood
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.MoodCard
import com.example.moodtunes.components.SearchBar
import com.example.moodtunes.components.PageSelected

@Composable
fun SelectMoodPages(navController: NavController) {
    val popularMoods = listOf(Mood.Happy, Mood.Sad)
    var searchText by remember { mutableStateOf("") }
    val allMoods = listOf(
        Mood.Angry,
        Mood.Calm,
        Mood.Energetic,
        Mood.Romantic,
        Mood.Sleepy,
        Mood.Focused,
        Mood.Chill,
        Mood.Uplifted,
        Mood.Nostalgic,
        Mood.Hopeful,
        Mood.Playful,
        Mood.Motivated
    )

    Scaffold (
        bottomBar = { BottomBar(navController, PageSelected.Home) },
        content = { innerPadding ->
            val recentMoods = listOf(Mood.Happy, Mood.Calm, Mood.Energetic)

            Background {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(horizontal = 8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "How are you feeling today?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Select a mood to discover your perfect soundtrack",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SearchBar(
                            placeholder = "Search for a mood...",
                            searchText = searchText,
                            onTextChange = { newText -> searchText = newText }
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    val filteredPopularMoods = popularMoods.filter {
                        searchText.isBlank() || it.name.contains(searchText, ignoreCase = true)
                    }

                    if (filteredPopularMoods.isNotEmpty()) {
                        item {
                            SectionHeader(icon = "🔥", title = "Popular Moods")
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                filteredPopularMoods.forEach { mood ->
                                    MoodCard(
                                        mood = mood,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            navController.navigate("select-kind-of-music/${mood.name}")
                                        }
                                    )
                                }
                                if (filteredPopularMoods.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    val filteredAllMoods = allMoods.filter {
                        searchText.isBlank() || it.name.contains(searchText, ignoreCase = true)
                    }

                    if (filteredAllMoods.isNotEmpty()) {
                        item {
                            SectionHeader(icon = "🎭", title = "All Moods")
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(filteredAllMoods.chunked(2)) { moodPair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                moodPair.forEach { mood ->
                                    MoodCard(
                                        mood = mood,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            navController.navigate("select-kind-of-music/${mood.name}")
                                        }
                                    )
                                }
                                if (moodPair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (searchText.isBlank()) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            SectionHeader(icon = "🕐", title = "Recently Used")

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                recentMoods.forEach { mood ->
                                    RecentMoodPill(mood = mood)
                                }
                            }
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SectionHeader(icon: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun RecentMoodPill(mood: Mood) {
    val icon = MOOD_ICONS[mood] ?: "❓"

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = mood.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
