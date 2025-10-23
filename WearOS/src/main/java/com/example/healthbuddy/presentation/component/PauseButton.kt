package com.example.healthbuddy.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import com.example.healthbuddy.R

@Composable
fun PauseButton(onPauseClick: () -> Unit) {
    FilledIconButton(
        onClick = onPauseClick
    ) {
        Icon(
            imageVector = Icons.Default.Pause,
            contentDescription = stringResource(id = R.string.pause_button_cd)
        )
    }
}

@Preview
@Composable
fun PauseButtonPreview() {
    PauseButton { }
}
