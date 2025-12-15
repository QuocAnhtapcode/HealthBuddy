package com.example.healthbuddy.screens.workout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.data.model.MuscleGroup
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
    onOpenExercisePicker: (MuscleGroup) -> Unit,     // navigate("workout/exercises/${group.id}")
    onOpenExerciseDetail: (Long) -> Unit            // navigate("workout/detail/$exerciseId") nếu muốn
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodaySession()
    }

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
        ui.isRestDay -> {
            RestDayScreen()
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
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.loadTodaySession() },
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
                    "No workout session for today.",
                    color = TextSecondary
                )
            }
        }

        else -> {
            val session = ui.todaySession!!
            val planSession = session.planSession
            val muscleGroups = planSession.muscleGroups

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
            ) {
                // ---------- STATUS HEADER ----------
                TodayWorkoutStatusHeader(
                    session = session,
                    userActivityLevel = userActivityLevel
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mỗi card tương ứng 1 nhóm cơ trong buổi tập
                    items(muscleGroups, key = { it.id }) { group ->
                        val exercisesForGroup = session.sessionExercises.filter { sessionExercise ->
                            sessionExercise.exercise.muscleGroups.any { mg -> mg.id == group.id }
                        }

                        MuscleGroupCard(
                            muscleGroup = group,
                            sessionExercises = exercisesForGroup,
                            onAddClick = { onOpenExercisePicker(group) },
                            onExerciseClick = { exId -> onOpenExerciseDetail(exId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayWorkoutStatusHeader(
    session: TodayWorkoutSession,
    userActivityLevel: String
) {
    val target = session.planSession.targetCalories
    val estimated = session.estimatedCalories
    val ratioRaw = if (target > 0f) estimated / target else 0f
    val progress by animateFloatAsState(
        targetValue = ratioRaw.coerceIn(0f, 1f),
        label = "sessionProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Workout today",
            color = AccentLime,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = session.planSession.sessionDayOfWeek.lowercase()
                .replaceFirstChar { it.titlecase() },
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(LavenderBand)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = session.planSession.category.lowercase()
                            .replaceFirstChar { it.titlecase() },
                        color = TextPrimary.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${estimated.toInt()} / ${target.toInt()} kcal",
                        color = AccentLime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Estimated vs target",
                        color = TextPrimary.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Level",
                        color = TextSecondary.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = userActivityLevel.replaceFirstChar { it.titlecase() },
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${session.sessionExercises.size} exercises",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.18f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(999.dp))
                        .background(AccentLime)
                )
            }
        }
    }
}

@Composable
private fun MuscleGroupCard(
    muscleGroup: MuscleGroup,
    sessionExercises: List<SessionExercise>,
    onAddClick: () -> Unit,
    onExerciseClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(BackgroundDark),
            ) {
                Text(
                    text = muscleGroup.name,
                    color = AccentLime,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // Nút Add tròn giống bản vẽ
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundDark)
                    .border(2.dp, AccentLime, CircleShape)
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add exercise",
                    tint = AccentLime
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (sessionExercises.isEmpty()) {
            Text(
                text = "No exercises added yet for this muscle group.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sessionExercises.forEach { item ->
                    SessionExerciseRow(
                        sessionExercise = item,
                        onClick = { onExerciseClick(item.exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionExerciseRow(
    sessionExercise: SessionExercise,
    onClick: () -> Unit
) {
    val exercise = sessionExercise.exercise

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundDark.copy(alpha = 0.75f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(LavenderBand.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = BackgroundDark
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${sessionExercise.estimatedCalories.toInt()} kcal",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Text(
            text = exercise.difficulty.replaceFirstChar { it.titlecase() },
            color = AccentLime,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
