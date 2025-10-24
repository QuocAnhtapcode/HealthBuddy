package com.example.healthbuddy.screens.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.ui.theme.InputContainer
import com.example.healthbuddy.ui.theme.InputText

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        color = InputText,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}
@Composable
fun tfColorsCommon() = TextFieldDefaults.colors(
    focusedContainerColor = InputContainer,
    unfocusedContainerColor = InputContainer,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = InputText
)
