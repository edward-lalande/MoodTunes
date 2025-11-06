package com.example.moodtunes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MoodTunesTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    placeholder: String? = null,
    textColor: Color = Color.Black,
    placeholderColor: Color = Color.Gray,
    outlineColor: Color = Color.Black,
    fillColor: Color = Color.White,
    backgroundColor: Color = Color.White,
    iconColor: Color = Color.Gray,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)).background(backgroundColor)
            .border(2.dp, outlineColor, RoundedCornerShape(16.dp)),
        placeholder = {
            if (placeholder != null) {
                Text(text = placeholder, color = placeholderColor)
            }
        },
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "Icon",
                    tint = iconColor
                )
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Icon",
                    tint = iconColor
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = fillColor,
            focusedContainerColor = fillColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = outlineColor,
            unfocusedBorderColor = outlineColor,
            focusedLabelColor = outlineColor,
            unfocusedLabelColor = placeholderColor,
            cursorColor = outlineColor
        ),
    )
}
