package com.example.healthbuddy.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.healthbuddy.ui.theme.TextSecondary

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
                        text = "Thêm bài tập",
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
                                    text = "Những bài tập này dành cho mức ${selectedLevel.name.lowercase()}.",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Mức độ của bạn đang là " +
                                        userLevelEnum.name.lowercase() +
                                        ". Hãy chọn bài tập phù hợp với bản thân",
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
                                text = "Không có bài tập cho nhóm cơ này.",
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
                                    onClick = { onOpenExerciseDetail(exercise) }
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
    onClick: () -> Unit
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
        if (!exercise.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = exercise.imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(60.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
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
    }
}
