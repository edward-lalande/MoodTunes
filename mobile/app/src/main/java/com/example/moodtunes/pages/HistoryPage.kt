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
import MOOD_NAME_TO_MOOD_OBJ
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import api
import com.example.moodtunes.DataObject.DeleteMusicHistoryReq
import com.example.moodtunes.DataObject.DeleteMusicHistoryResp
import com.example.moodtunes.DataObject.MusicHistory
import com.example.moodtunes.DataObject.MusicHistoryList
import com.example.moodtunes.components.Background
import com.example.moodtunes.components.BottomBar
import com.example.moodtunes.components.MoodCardWithSong
import com.example.moodtunes.components.MoodTunesButtonField
import com.example.moodtunes.components.PageSelected
import com.example.moodtunes.components.SearchBar
import com.example.moodtunes.components.TopBar
import com.example.moodtunes.pages.DateHistory
import com.example.moodtunes.storage.JWTHandler
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoryPage(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val scrollState = rememberScrollState()
    var history by remember { mutableStateOf<List<MusicHistory>>(emptyList()) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val token = JWTHandler().getToken(context)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val response = api.request<MusicHistoryList?>(
                method = "GET",
                url = "http://192.168.200.176:8080/music/history",
                jsonBody = null,
                token = token
            )
            if (response != null) {
                history = response.history
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    fun isToday(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return false
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply { time = date }
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return false
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val target = Calendar.getInstance().apply { time = date }
        return yesterday.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isThisWeek(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return false
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
               calendar.get(Calendar.YEAR) == currentYear
    }

    fun isThisMonth(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return false
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.time = date
        return calendar.get(Calendar.MONTH) == currentMonth &&
               calendar.get(Calendar.YEAR) == currentYear
    }

    fun getCategory(dateString: String): String {
        return when {
            isToday(dateString) -> "Today"
            isYesterday(dateString) -> "Yesterday"
            isThisWeek(dateString) -> "This Week"
            isThisMonth(dateString) -> "This Month"
            else -> "Older"
        }
    }

    val filteredHistory = history.filter { item ->
        (searchText.isEmpty() || item.title.contains(searchText, ignoreCase = true) || item.artist.contains(searchText, ignoreCase = true)) &&
        (selectedFilter == "All" || getCategory(item.date) == selectedFilter || (selectedFilter == "Recent" && (isToday(item.date) || isYesterday(item.date))))
    }

    val groupedItems = filteredHistory.groupBy { getCategory(it.date) }
    val categoryOrder = listOf("Today", "Yesterday", "This Week", "This Month", "Older")

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    SearchBar(
                        placeholder = "Search your moods...",
                        searchText = searchText,
                        onTextChange = { searchText = it },
                    )
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .horizontalScroll(scrollState)
                    ) {
                        val filters = listOf("All", "Recent", "Today", "This Week", "This Month")
                        filters.forEach { label ->
                            MoodTunesButtonField(
                                onClick = { selectedFilter = label },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .height(36.dp),
                                contentColor = Color.White,
                                backgroundColor = if (selectedFilter == label)
                                    Color(0xFF5B21B6)
                                else
                                    Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                        ) {
                            categoryOrder.forEach { category ->
                                val itemsForCategory = groupedItems[category]
                                if (!itemsForCategory.isNullOrEmpty()) {
                                    item {
                                        val iconRes = when (category) {
                                            "Today" -> R.drawable.calendar
                                            "Yesterday" -> R.drawable.clock
                                            "This Week" -> R.drawable.week
                                            "This Month" -> R.drawable.month
                                            else -> R.drawable.calendar
                                        }
                                        DateHistory(
                                            dateText = category,
                                            icon = iconRes,
                                            iconDescription = "$category's mood"
                                        )
                                    }

                                    items(
                                        count = itemsForCategory.size
                                    ) { index ->
                                        val item = itemsForCategory[index]
                                        val moodEnum = MOOD_NAME_TO_MOOD_OBJ.entries.find {
                                            it.key.equals(item.mood, ignoreCase = true) 
                                        }?.value

                                        MoodCardWithSong(
                                            songTitle = item.title,
                                            songArtist = item.artist,
                                            albumCoverUrl = item.albumCoverUrl,
                                            mood = moodEnum ?: Mood.Happy,
                                            onDelete = {
                                                coroutineScope.launch {
                                                    isLoading = true

                                                    api.request<DeleteMusicHistoryResp?>(
                                                        method = "DELETE",
                                                        url = "http://192.168.200.176:8080/music/history",
                                                        jsonBody = Gson().toJson(DeleteMusicHistoryReq(item.id)),
                                                        token = token,
                                                    )

                                                    val response = api.request<MusicHistoryList?>(
                                                        method = "GET",
                                                        url = "http://192.168.200.176:8080/music/history",
                                                        jsonBody = null,
                                                        token = token
                                                    )
                                                    if (response != null) {
                                                        history = response.history
                                                    }
                                                    isLoading = false
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
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
            .padding(vertical = 16.dp, horizontal = 8.dp)
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
