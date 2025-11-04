package com.example.moodtunes.components

import MOOD_COLOR
import MOOD_ICONS
import Mood
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

@Composable
fun MoodCard(
    mood: Mood,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}

) {
    val backgroundColor = Color((MOOD_COLOR[mood] ?: "#000000").toColorInt())
    val vibrantColor = makeColorVibrant(backgroundColor)
    val icon = MOOD_ICONS[mood] ?: "❓"
    val moodName = mood.name

    Card(
        modifier = modifier
            .width(180.dp)
            .height(110.dp)
            .padding(5.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            vibrantColor,
                            vibrantColor.copy(alpha = 0.95f),
                            vibrantColor.copy(alpha = 0.85f)
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 200f
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = moodName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = icon,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = getMoodDescription(mood),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

private fun makeColorVibrant(color: Color): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.rgb(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        ),
        hsv
    )

    hsv[1] = (hsv[1] * 1.3f).coerceIn(0f, 1f)

    if (hsv[2] < 0.6f) {
        hsv[2] = (hsv[2] * 1.2f).coerceIn(0f, 1f)
    }

    val vibrantColorInt = android.graphics.Color.HSVToColor(hsv)
    return Color(vibrantColorInt)
}

private fun getMoodDescription(mood: Mood): String {
    return when (mood) {
        Mood.Happy -> "Joyful"
        Mood.Energetic -> "Dynamic"
        Mood.Calm -> "Peaceful & serene"
        Mood.Sad -> "Melancholy"
        Mood.Romantic -> "Loving"
        Mood.Motivated -> "Driven"
        Mood.Focused -> "Concentrated"
        Mood.Chill -> "Relaxed"
        Mood.Uplifted -> "Elevated"
        Mood.Angry -> "Intense & powerful"
        Mood.Anxious -> "Worried"
        Mood.Nostalgic -> "Reflective"
        Mood.Playful -> "Fun"
        Mood.Bored -> "Uninspired"
        Mood.Confused -> "Uncertain"
        Mood.Hopeful -> "Optimistic"
        Mood.Grateful -> "Thankful"
        Mood.Lonely -> "Solitary"
        Mood.Adventurous -> "Bold"
        Mood.Sleepy -> "Drowsy"
        Mood.Satisfied -> "Content"
        Mood.Tense -> "Stressed"
        Mood.Ecstatic -> "Euphoric"
        Mood.Melancholic -> "Pensive"
    }
}
