package com.example.healthbuddy.presentation.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.ConfirmationDialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.healthbuddy.R
import com.example.healthbuddy.presentation.theme.ThemePreview

@Composable
fun ExerciseNotAvailable() {
    var showConfirmation by remember { mutableStateOf(true) }

    ConfirmationDialog(
        visible = showConfirmation,
        onDismissRequest = { showConfirmation = false },
        text = { Text(text = stringResource(id = R.string.not_avail)) }
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = stringResource(id = R.string.not_avail)
        )
    }
}

@WearPreviewDevices
@Composable
fun ExerciseNotAvailablePreview() {
    ThemePreview {
        ExerciseNotAvailable()
    }
}
