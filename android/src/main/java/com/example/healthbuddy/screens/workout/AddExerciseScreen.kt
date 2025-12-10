package com.example.healthbuddy.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.R
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.InputContainer
import com.example.healthbuddy.ui.theme.InputText
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    viewModel: WorkoutViewModel,
    exerciseId: Long,
    onBack: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()
    val exercise = ui.exercises.firstOrNull { it.id == exerciseId } ?: return

    var mode by remember {
        mutableStateOf(
            if (exercise.unit == "MET") "TIME" else "STRENGTH"
        )
    }
    var hoursText by remember { mutableStateOf("1") }
    var repsText by remember { mutableStateOf("12") }
    var setsText by remember { mutableStateOf("3") }
    var weightText by remember { mutableStateOf("30") }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text(exercise.name, color = TextPrimary, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = null,
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            // image
            AsyncImage(
                model = exercise.imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(4.dp))

            // mode chips
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .padding(4.dp)
            ) {
                ModeChip("Time (hours)", mode == "TIME") { mode = "TIME" }
                ModeChip("Reps / Sets", mode == "STRENGTH") { mode = "STRENGTH" }
            }

            Spacer(Modifier.height(16.dp))

            if (mode == "TIME") {
                LabeledNumberField("Hours", hoursText) { hoursText = it }
            } else {
                LabeledNumberField("Reps", repsText) { repsText = it }
                LabeledNumberField("Sets", setsText) { setsText = it }
                LabeledNumberField("Weight (kg)", weightText) { weightText = it }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (mode == "TIME") {
                        val h = hoursText.toFloatOrNull() ?: 1f
                        viewModel.addExerciseAsDuration(exerciseId, h) { onBack() }
                    } else {
                        val reps = repsText.toIntOrNull() ?: 12
                        val sets = setsText.toIntOrNull() ?: 3
                        val w = weightText.toFloatOrNull() ?: 30f
                        viewModel.addExerciseAsStrength(exerciseId, reps, sets, w) { onBack() }
                    }
                },
                enabled = !ui.addingExercise,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg)
            ) {
                if (ui.addingExercise) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AccentLime,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add to session", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 3.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) AccentLime else SurfaceDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (selected) BackgroundDark else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LabeledNumberField(
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        TextField(
            value = text,
            onValueChange = { value -> onTextChange(value.filter { it.isDigit() || it == '.' }) },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputContainer,
                unfocusedContainerColor = InputContainer,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                cursorColor = InputText,
                focusedTextColor = InputText,
                unfocusedTextColor = InputText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        )
    }
}

