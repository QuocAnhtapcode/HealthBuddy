package com.example.healthbuddy.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import com.example.healthbuddy.R

@Composable
fun StartButton(onStartClick: () -> Unit) {
    FilledIconButton(
        onClick = onStartClick
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(id = R.string.start_button_cd)
        )
    }
}

@Preview
@Composable
fun StartButtonPreview() {
    StartButton { }
}
