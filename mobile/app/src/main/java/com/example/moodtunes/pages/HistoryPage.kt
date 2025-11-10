package com.example.moodtunes.pages

import Mood
import androidx.compose.foundation.horizontalScroll
import com.example.moodtunes.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.MoodCardWithSong
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.PageSelected
import com.example.moodtunes.components.SearchBar
import com.example.moodtunes.components.TopBar

data class MoodHistoryEntry(
    val mood: Mood,
    val date: String,
    val songTitle: String,
    val songArtist: String,
    val songImageUrl: String
)

@Composable
fun HistoryPage(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Recent") }
    val scrollState = rememberScrollState()

    val allItems = listOf(
        MoodHistoryEntry(
            mood = Mood.Happy,
            date = "Today",
            songTitle = "Good 4 U",
            songArtist = "Olivia Rodrigo",
            songImageUrl = "R.drawable.song_placeholder"
        ),
        MoodHistoryEntry(
            mood = Mood.Energetic,
            date = "Today",
            songTitle = "Beast Mode Workout",
            songArtist = "DJ Khaled",
            songImageUrl = "R.drawable.song_placeholder"
        ),
        MoodHistoryEntry(
            mood = Mood.Melancholic,
            date = "Yesterday",
            songTitle = "Someone Like You",
            songArtist = "Adele",
            songImageUrl = "R.drawable.song_placeholder"
        ),
        MoodHistoryEntry(
            mood = Mood.Romantic,
            date = "This Week",
            songTitle = "Bank On It",
            songArtist = "Burna Boy",
            songImageUrl = "R.drawable.song_placeholder"
        ),
        MoodHistoryEntry(
            mood = Mood.Calm,
            date = "This Month",
            songTitle = "He Loves Us Both",
            songArtist = "Lila Ike",
            songImageUrl = "R.drawable.song_placeholder"
        )
    )

    val filteredItems = when (selectedFilter) {
        "All" -> allItems
        "Today" -> allItems.filter { it.date in listOf("Today", "Yesterday") }
        "This Week" -> allItems.filter { it.date == "This Week" }
        "This Month" -> allItems.filter { it.date in listOf("This Week", "This Month") }
        else -> allItems
    }

    val groupedItems = filteredItems.groupBy { it.date }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Mood History",
                backRoute = "select-mood",
                backDescription = "Go back to select mood"
            )
        },
        content = { innerPadding ->
            Background {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp)
                ) {

                    item {
                        SearchBar(
                            placeholder = "Search your moods...",
                            searchText = searchText,
                            onTextChange = { searchText = it }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .horizontalScroll(scrollState)
                        ) {
                            val buttonConfigs = listOf(
                                "Recent" to 75.dp,
                                "Today" to 90.dp,
                                "This Week" to 105.dp,
                                "This Month" to 190.dp
                            )
                            buttonConfigs.forEach { (label) ->
                                MoodTunesButtonField(
                                    onClick = { selectedFilter = label },
                                    modifier = Modifier.padding(5.dp),
                                    contentColor = Color.White,
                                    backgroundColor = if (selectedFilter == label)
                                        Color(0xFF5B21B6)
                                    else
                                        Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(30.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    groupedItems.forEach { (date, itemsForDate) ->
                        item {
                            val iconRes = when (date) {
                                "Today" -> R.drawable.calendar
                                "Yesterday" -> R.drawable.clock
                                "This Week" -> R.drawable.week
                                "This Month" -> R.drawable.month
                                else -> R.drawable.calendar
                            }
                            DateHistory(
                                dateText = date,
                                icon = iconRes,
                                iconDescription = "$date's mood"
                            )
                        }

                        items(
                            count = itemsForDate.size
                        ) { index ->
                            val item = itemsForDate[index]
                            MoodCardWithSong(
                                mood = item.mood,
                                songTitle = item.songTitle,
                                songArtist = item.songArtist,
                                songImageUrl = item.songImageUrl,
                                onDelete = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        bottomBar = { BottomBar(navController, PageSelected.History) }
    )
}


@Composable
fun DateHistory(dateText: String, icon: Int, iconDescription: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = iconDescription,
                tint = Color(0xFF5B21B6),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(35.dp)
            )
            Text(
                text = dateText,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}