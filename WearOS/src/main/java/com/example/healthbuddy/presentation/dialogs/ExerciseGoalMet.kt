package com.example.healthbuddy.presentation.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.ConfirmationDialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.healthbuddy.R

@Composable
fun ExerciseGoalMet(
    showDialog: Boolean
) {
    ConfirmationDialog(
        visible = showDialog,
        onDismissRequest = { },
        text = { Text(text = stringResource(id = R.string.goal_achieved)) }
    ) {
        Icon(
            Icons.Default.SportsScore,
            contentDescription = stringResource(id = R.string.goal_achieved)
        )
    }
}

@WearPreviewDevices
@Composable
fun ExerciseGoalMetPreview() {
    ExerciseGoalMet(true)
}
