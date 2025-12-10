package com.example.healthbuddy.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.data.model.SessionExercise
import com.example.healthbuddy.data.model.TodayWorkoutSession
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayWorkoutScreen(
    viewModel: WorkoutViewModel,
    userActivityLevel: String,
    onOpenExercisePicker: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodaySession()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        when {
            ui.loadingSession -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentLime)
                }
            }

            ui.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = ui.error ?: "Something went wrong",
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadTodaySession()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Retry", color = TextPrimary)
                        }
                    }
                }
            }

            ui.todaySession == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workout session for today.",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                val session = ui.todaySession!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark)
                ) {

                    WorkoutHeaderBand(
                        session = session,
                        activityLevel = userActivityLevel,
                        onAddExerciseClick = onOpenExercisePicker
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Exercises",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(6.dp))

                    if (session.sessionExercises.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No exercises yet. Tap \"Add exercise\" to start this session.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            items(session.sessionExercises, key = { it.id }) { sessionExercise ->
                                SessionExerciseRow(sessionExercise)
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutHeaderBand(
    session: TodayWorkoutSession,
    activityLevel: String,
    onAddExerciseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // hàng level
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today session",
                color = AccentLime,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(LavenderBand)
                .padding(14.dp)
        ) {
            Text(
                text = session.planSession.category.lowercase()
                    .replaceFirstChar { it.uppercase() },
                color = BackgroundDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Target ${session.planSession.targetCalories.toInt()} kcal",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Estimated ${session.estimatedCalories.toInt()} kcal • ${session.sessionExercises.size} exercises",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Status",
                        color = BackgroundDark.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = session.status.replace('_', ' ')
                            .lowercase()
                            .replaceFirstChar { it.uppercase() },
                        color = BackgroundDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onAddExerciseClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Add exercise", color = AccentLime, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SessionExerciseRow(
    sessionExercise: SessionExercise
) {
    val exercise = sessionExercise.exercise

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            if (!exercise.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = exercise.name.firstOrNull()?.uppercase() ?: "",
                    color = AccentLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${sessionExercise.estimatedCalories.toInt()} kcal",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = exercise.category.lowercase()
                    .replaceFirstChar { it.uppercase() },
                color = AccentLime,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = when {
                    sessionExercise.hours != null -> "${sessionExercise.hours} h"
                    sessionExercise.reps != null && sessionExercise.sets != null ->
                        "${sessionExercise.sets} x ${sessionExercise.reps}"
                    else -> exercise.unit
                },
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
