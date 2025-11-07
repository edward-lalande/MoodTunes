package com.example.moodtunes.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(placeholder: String, searchText: String, onTextChange: (String) -> Unit) {
    MoodTunesTextField(
        text = searchText,
        onTextChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        leadingIcon = Icons.Default.Search,
        placeholder = placeholder,
        textColor = Color.White,
        placeholderColor = Color.White.copy(alpha = 0.4f),
        outlineColor = Color.White.copy(alpha = 0.01f),
        fillColor = Color.White.copy(alpha = 0.05f),
        backgroundColor = Color.Transparent,
        iconColor = Color.White.copy(alpha = 0.4f)
    )
}