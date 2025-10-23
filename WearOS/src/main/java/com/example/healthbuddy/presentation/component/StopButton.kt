package com.example.healthbuddy.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import com.example.healthbuddy.R

@Composable
fun StopButton(onEndClick: () -> Unit) {
    FilledIconButton(
        onClick = onEndClick
    ) {
        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = stringResource(id = R.string.stop_button_cd)
        )
    }
}

@Preview
@Composable
fun StopButtonPreview() {
    StopButton { }
}
