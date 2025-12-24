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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    onOpenExercisePicker: (MuscleGroup) -> Unit,
    onOpenExerciseDetail: (Long) -> Unit
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
                Text("Chưa có lịch tập cho hôm nay.", color = TextSecondary)
            }
        }

        else -> {
            val session = ui.todaySession!!
            val planSession = session.planSession
            val muscleGroups = planSession.muscleGroups

            // chọn group mặc định: group đầu tiên
            var selectedGroup by remember(session.id) {
                mutableStateOf(muscleGroups.firstOrNull())
            }

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

                // ---------- FILTER BAR (Dropdown + Add) ----------
                FilterMuscleGroupBar(
                    muscleGroups = muscleGroups,
                    selectedGroup = selectedGroup,
                    onSelect = { selectedGroup = it },
                    onAdd = {
                        selectedGroup?.let { onOpenExercisePicker(it) }
                    }
                )

                Spacer(Modifier.height(10.dp))

                // ---------- LIST EXERCISES USER ADDED ----------
                val addedExercises = session.sessionExercises

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    item {
                        Text(
                            text = "Thêm bài tập",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                    }

                    if (addedExercises.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(SurfaceDark)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Chưa có bài tập.",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Chọn nhóm cơ và tiến hành thêm bài tập.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(addedExercises, key = { it.id }) { item ->
                            SessionExerciseRow(
                                sessionExercise = item,
                                onClick = { onOpenExerciseDetail(item.exercise.id) },
                                onDelete = {
                                    viewModel.deleteExerciseInSession(item.id)
                                }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterMuscleGroupBar(
    muscleGroups: List<MuscleGroup>,
    selectedGroup: MuscleGroup?,
    onSelect: (MuscleGroup) -> Unit,
    onAdd: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .menuAnchor()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BackgroundDark.copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedGroup?.name ?: "Chọn nhóm cơ",
                        color = if (selectedGroup == null) TextSecondary else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(SurfaceDark)
            ) {
                muscleGroups.forEach { g ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = g.name,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            onSelect(g)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BackgroundDark)
                .border(2.dp, AccentLime, CircleShape)
                .clickable(enabled = selectedGroup != null) { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm bài tập",
                tint = AccentLime
            )
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
        label = "Tiến độ tập luyện"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Lịch tập hôm nay",
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
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${estimated.toInt()} / ${target.toInt()} kcal",
                        color = AccentLime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = userActivityLevel.replaceFirstChar { it.titlecase() },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionExerciseRow(
    sessionExercise: SessionExercise,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val exercise = sessionExercise.exercise
    val name = exercise.name

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteConfirm = true
                false // không dismiss item ngay
            } else false
        }
    )

    LaunchedEffect(showDeleteConfirm) {
        if (!showDeleteConfirm && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SurfaceDark,
            title = { Text("Xác nhận xóa", color = TextPrimary) },
            text = { Text("Bạn có muốn xóa \"$name\" không?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy", color = TextPrimary)
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val isDismissing = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val alpha by animateFloatAsState(if (isDismissing) 1f else 0f, label = "alpha")
            val scale by animateFloatAsState(if (isDismissing) 1.2f else 0.8f, label = "scale")

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDismissing) Color.Red.copy(alpha = 0.8f) else Color.Transparent)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.White,
                    modifier = Modifier
                        .scale(scale)
                        .graphicsLayer(alpha = alpha)
                )
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundDark.copy(alpha = 0.7f))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!exercise.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = exercise.name,
                    modifier = Modifier.size(60.dp),
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

                val detail = buildString {
                    append("${sessionExercise.estimatedCalories.toInt()} kcal")
                    sessionExercise.reps?.let { r -> append(" · $r reps") }
                    sessionExercise.sets?.let { s -> append(" · $s sets") }
                    sessionExercise.weightUsed?.let { w -> append(" · ${w.toInt()} kg") }
                    sessionExercise.hours?.let { h -> append(" · ${"%.2f".format(h)} h") }
                }

                Text(
                    text = detail,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
