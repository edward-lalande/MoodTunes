package com.example.moodtunes.components

import androidx.compose.material3.Button
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun MoodTunesButtonField(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Blue,
    contentColor: Color = Color.White,
    disabledContentColor: Color = Color.Gray,
    disabledContainerColor: Color = Color.Black,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    icon: ImageVector? = null,
    textStyle: TextStyle = TextStyle.Default
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
            disabledContainerColor = disabledContainerColor
        ),
        shape = shape,
        enabled = enabled,
        border = border,
        elevation = elevation,
    ) {
        if (icon != null) {
            Icon(
                modifier = Modifier.padding(end = 4.dp).offset(x = (-4).dp),
                imageVector = icon,
                contentDescription = null
            )
        }

        Text(
            text = text,
            style = textStyle
        )
    }
}