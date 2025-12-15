package com.example.healthbuddy.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.data.model.*
import com.example.healthbuddy.ui.theme.*
import com.example.healthbuddy.R

enum class ActivityLevel(val value: String) {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    ADVANCED("advanced")
}

private fun ActivityLevel.rank(): Int = when (this) {
    ActivityLevel.BEGINNER -> 0
    ActivityLevel.INTERMEDIATE -> 1
    ActivityLevel.ADVANCED -> 2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerScreen(
    viewModel: WorkoutViewModel,
    userActivityLevel: String,   // "beginner" | "intermediate" | "advanced"
    muscleGroupId: Long,
    onBack: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onOpenExerciseDetail: (Exercise) -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    val userLevelEnum = remember(userActivityLevel) {
        when (userActivityLevel.lowercase()) {
            "intermediate" -> ActivityLevel.INTERMEDIATE
            "advanced" -> ActivityLevel.ADVANCED
            else -> ActivityLevel.BEGINNER
        }
    }

    var selectedLevel by remember { mutableStateOf(userLevelEnum) }

    val isLevelTooHigh = selectedLevel.rank() > userLevelEnum.rank()

    LaunchedEffect(muscleGroupId, selectedLevel) {
        if (!isLevelTooHigh) {
            viewModel.loadExercisesForToday(
                userLevel = selectedLevel.value,
                groupId = muscleGroupId
            )
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add exercise",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
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
            // -------- Level Tabs (Beginner / Intermediate / Advanced) --------
            LevelSegmentControl(
                selected = selectedLevel,
                onSelect = { selectedLevel = it }
            )

            Spacer(Modifier.height(12.dp))

            // -------- Content Card --------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(SurfaceDark)
                    .padding(14.dp)
            ) {
                when {
                    isLevelTooHigh -> {
                        // Cảnh báo khi chọn level cao hơn level user
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = AccentLime,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "These exercises are for ${selectedLevel.name.lowercase()} level.",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Your current level is " +
                                        userLevelEnum.name.lowercase() +
                                        ". Please progress gradually for safety.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }

                    ui.loadingExercises -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentLime)
                        }
                    }

                    ui.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = ui.error ?: "Error loading exercises",
                                    color = Color.Red
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Tap level again to retry.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    ui.exercises.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No exercises available for this muscle group.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(ui.exercises, key = { it.id }) { exercise ->
                                ExercisePickerRow(
                                    exercise = exercise,
                                    onClick = { onOpenExerciseDetail(exercise) },
                                    onAddClick = { onExerciseSelected(exercise) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelSegmentControl(
    selected: ActivityLevel,
    onSelect: (ActivityLevel) -> Unit
) {
    val items = listOf(
        ActivityLevel.BEGINNER,
        ActivityLevel.INTERMEDIATE,
        ActivityLevel.ADVANCED
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceDark),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { level ->
            val isSelected = level == selected
            val bg = if (isSelected) AccentLime else Color.Transparent
            val textColor = if (isSelected) BackgroundDark else TextPrimary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(bg)
                    .clickable { onSelect(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.name.lowercase()
                        .replaceFirstChar { it.titlecase() },
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ExercisePickerRow(
    exercise: Exercise,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BackgroundDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play / thumbnail
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(LavenderBand),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = BackgroundDark
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
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
                text = exercise.muscleGroups.joinToString { it.name },
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = exercise.difficulty.replaceFirstChar { it.titlecase() },
            color = AccentLime,
            fontSize = 11.sp,
            modifier = Modifier.padding(end = 6.dp)
        )

        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentLime
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("Add", color = BackgroundDark, fontSize = 11.sp)
        }
    }
}
