package com.example.healthbuddy.screens.test

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.healthbuddy.PhoneViewModel

@Composable
fun TestScreen(viewModel: PhoneViewModel) {
    val exerciseSummary by viewModel.message.collectAsState()
    Text(
        text = exerciseSummary
    )
}
