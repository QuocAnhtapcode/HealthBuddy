package com.example.healthbuddy.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.Exercise
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    viewModel: WorkoutViewModel,
    exerciseId: Long,
    isUpdateScreen: Boolean = false,
    onBack: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    // Tìm exercise từ list filter hiện có hoặc từ session
    val exercise = ui.exercises.firstOrNull { it.id == exerciseId }
        ?: ui.todaySession
            ?.sessionExercises
            ?.firstOrNull { it.exercise.id == exerciseId }
            ?.exercise

    if (exercise == null) {
        // Không tìm thấy bài tập -> quay lại
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val sessionExercise = ui.todaySession
        ?.sessionExercises
        ?.firstOrNull { it.exercise.id == exerciseId }

    val sessionExerciseId = sessionExercise?.id

    val isDurationBased =
        exercise.unit.equals("MET", ignoreCase = true) ||
            exercise.unit.equals("HOUR", ignoreCase = true)

    var hoursText by remember { mutableStateOf("1.0") }

    var repsText by remember { mutableStateOf("12") }
    var setsText by remember { mutableStateOf("3") }
    var weightText by remember { mutableStateOf("20") }

    val adding = ui.addingExercise

    LaunchedEffect(isUpdateScreen, exerciseId) {
        val se = ui.todaySession?.sessionExercises?.firstOrNull { it.exercise.id == exerciseId }
        if (isUpdateScreen && se != null) {
            if (isDurationBased) {
                hoursText = (se.hours ?: 1f).toString()
            } else {
                repsText = (se.reps ?: 12).toString()
                setsText = (se.sets ?: 3).toString()
                weightText = (se.weightUsed ?: 20f).toString()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exercise.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Quay lại",
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(LavenderBand)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!exercise.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = exercise.imageUrl,
                        contentDescription = exercise.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentLime,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            // ---------- Card thông tin + input ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(AccentLime.copy(alpha = 0.9f))
                    .padding(16.dp)
            ) {
                if (!exercise.description.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = exercise.description,
                        color = BackgroundDark.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
                if (isDurationBased) {
                    // --- Input theo thời gian ---
                    Text(
                        text = "Thời gian tập (giờ)",
                        color = BackgroundDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = hoursText,
                        onValueChange = { new ->
                            hoursText = new.filter { it.isDigit() || it == '.' }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = BackgroundDark
                        ),
                        textStyle = TextStyle(
                            color = BackgroundDark,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SmallNumberField(
                            label = "Reps",
                            value = repsText,
                            onValueChange = { repsText = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f)
                        )
                        SmallNumberField(
                            label = "Sets",
                            value = setsText,
                            onValueChange = { setsText = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f)
                        )
                        SmallNumberField(
                            label = "Trọng lượng (kg)",
                            value = weightText,
                            onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' } },
                            modifier = Modifier.weight(1.4f)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (isUpdateScreen) {
                            val sessionExercise = ui.todaySession
                                ?.sessionExercises
                                ?.firstOrNull { it.exercise.id == exerciseId }

                            val sessionExerciseId = sessionExercise?.id
                            if (sessionExerciseId == null) {
                                return@Button
                            }

                            if (isDurationBased) {
                                val h = hoursText.toFloatOrNull() ?: 0f
                                if (h > 0f) {
                                    viewModel.updateExerciseAsDuration(
                                        sessionExerciseId = sessionExerciseId,
                                        exerciseId = exercise.id,
                                        hours = h,
                                        onDone = onBack
                                    )
                                }
                            } else {
                                val reps = repsText.toIntOrNull() ?: 0
                                val sets = setsText.toIntOrNull() ?: 0
                                val weight = weightText.toFloatOrNull() ?: 0f
                                if (reps > 0 && sets > 0 && weight > 0f) {
                                    viewModel.updateExerciseAsStrength(
                                        sessionExerciseId = sessionExerciseId,
                                        exerciseId = exercise.id,
                                        reps = reps,
                                        sets = sets,
                                        weight = weight,
                                        onDone = onBack
                                    )
                                }
                            }
                        } else{
                            if (isDurationBased) {
                                val h = hoursText.toFloatOrNull() ?: 0f
                                if (h > 0f) {
                                    viewModel.addExerciseAsDuration(
                                        exerciseId = exercise.id,
                                        hours = h,
                                        onDone = onBack
                                    )
                                }
                            } else {
                                val reps = repsText.toIntOrNull() ?: 0
                                val sets = setsText.toIntOrNull() ?: 0
                                val weight = weightText.toFloatOrNull() ?: 0f
                                if (reps > 0 && sets > 0 && weight > 0f) {
                                    viewModel.addExerciseAsStrength(
                                        exerciseId = exercise.id,
                                        reps = reps,
                                        sets = sets,
                                        weight = weight,
                                        onDone = onBack
                                    )
                                }
                            }
                        }

                    },
                    enabled = !adding,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundDark,
                        disabledContainerColor = BackgroundDark.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(48.dp)
                        .fillMaxWidth(0.7f)
                ) {
                    if (adding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AccentLime,
                            strokeWidth = 2.dp
                        )
                    } else {
                        if(isUpdateScreen){
                            Text(
                                text = "Cập nhật",
                                color = AccentLime,
                                fontWeight = FontWeight.SemiBold
                            )
                        }else{
                            Text(
                                text = "Thêm vào kế hoạch",
                                color = AccentLime,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun SmallNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = BackgroundDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = BackgroundDark
            ),
            textStyle = TextStyle(
                color = BackgroundDark,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
    }
}
