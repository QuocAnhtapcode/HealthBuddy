package com.example.healthbuddy.presentation.preparing

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.health.services.client.data.LocationAvailability
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.curvedText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.healthbuddy.R
import com.example.healthbuddy.data.ServiceState
import com.example.healthbuddy.presentation.ambient.ambientGray
import com.example.healthbuddy.presentation.dialogs.ExerciseInProgressAlert
import com.example.healthbuddy.presentation.home.WatchCaloriesStat
import com.example.healthbuddy.presentation.home.WatchHomeViewModel
import com.example.healthbuddy.presentation.theme.AccentLime
import com.example.healthbuddy.presentation.theme.TextPrimary
import com.example.healthbuddy.presentation.theme.TextSecondary
import com.example.healthbuddy.presentation.theme.ThemePreview
import com.example.healthbuddy.service.ExerciseServiceState
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import kotlin.math.max

@Composable
fun PreparingExerciseRoute(
    onStart: () -> Unit,
    onFinishActivity: () -> Unit,
    onNoExerciseCapabilities: () -> Unit,
    onGoals: () -> Unit,
    watchHomeViewModel: WatchHomeViewModel = hiltViewModel()
) {
    val viewModel = hiltViewModel<PreparingViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    /** Request permissions prior to launching exercise.**/
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            Log.d(TAG, "All required permissions granted")
        }
    }

    LaunchedEffect(Unit) {
        watchHomeViewModel.startListening()
    }

    DisposableEffect(Unit) {
        onDispose { watchHomeViewModel.stopListening() }
    }

    SideEffect {
        val preparingState = uiState
        if (preparingState is PreparingScreenState.Preparing &&
            !preparingState.hasExerciseCapabilities
        ) {
            onNoExerciseCapabilities()
        }
    }

    if (uiState.serviceState is ServiceState.Connected) {
        val requiredPermissions = uiState.requiredPermissions
        LaunchedEffect(requiredPermissions) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    AmbientAware { ambientState ->
        PreparingExerciseScreen(
            onStart = {
                viewModel.startExercise()
                onStart()
            },
            uiState = uiState,
            onGoals = { onGoals() },
            ambientState = ambientState,
            watchHomeViewModel = watchHomeViewModel
        )
    }

    if (uiState.isTrackingInAnotherApp) {
        var dismissed by remember { mutableStateOf(false) }
        ExerciseInProgressAlert(
            onNegative = onFinishActivity,
            onPositive = { dismissed = true },
            showDialog = !dismissed
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun PreparingExerciseScreen(
    uiState: PreparingScreenState,
    ambientState: AmbientState,
    onStart: () -> Unit = {},
    onGoals: () -> Unit = {},
    watchHomeViewModel : WatchHomeViewModel
) {
    val location = (uiState as? PreparingScreenState.Preparing)?.locationAvailability
    val stats by watchHomeViewModel.stats.collectAsState()
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.BodyText,
        last = ColumnItemType.Button
    )
        ScreenScaffold(
            scrollState = columnState,
            timeText = {},
            contentPadding = contentPadding,
            modifier = Modifier
                .ambientGray(ambientState)
        ) { contentPadding ->
            LocationStatusText(
                updatePrepareLocationStatus(
                    locationAvailability = location ?: LocationAvailability.UNAVAILABLE
                )
            )
            TransformingLazyColumn(
                state = columnState,
                contentPadding = contentPadding
            ) {
                item {
                    CaloriesDashboard(
                        stats = stats,
                        ambient = ambientState is AmbientState.Ambient
                    )
                }

                /*
                item {
                    Text(
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(id = R.string.preparing_exercise),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 0.15f * LocalConfiguration.current.screenWidthDp.dp
                            )
                    )
                }

                 */
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompactButton(
                            label = { Text(
                                "Start exercise"
                            ) },
                            onClick = onStart
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompactButton(
                            label = { Text(stringResource(id = R.string.goal)) },
                            onClick = onGoals
                        )
                    }
                }
            }
        }
    }
@Composable
private fun updatePrepareLocationStatus(locationAvailability: LocationAvailability): String {
    val gpsText = when (locationAvailability) {
        LocationAvailability.ACQUIRED_TETHERED, LocationAvailability.ACQUIRED_UNTETHERED
        -> R.string.GPS_acquired
        LocationAvailability.NO_GNSS -> R.string.GPS_disabled
        // TODO Consider redirecting user to change device settings in this case
        LocationAvailability.ACQUIRING -> R.string.GPS_acquiring
        LocationAvailability.UNKNOWN -> R.string.GPS_initializing
        else -> R.string.GPS_unavailable
    }

    return stringResource(id = gpsText)
}

@Composable
private fun LocationStatusText(status: String) {
    CurvedLayout {
        curvedText(text = status, fontSize = 12.sp)
    }
}
/*
@WearPreviewDevices
@Composable
fun PreparingExerciseScreenPreview() {
    ThemePreview {
        PreparingExerciseScreen(
            uiState = PreparingScreenState.Preparing(
                serviceState = ServiceState.Connected(
                    ExerciseServiceState()
                ),
                isTrackingInAnotherApp = false,
                requiredPermissions = PreparingViewModel.permissions,
                hasExerciseCapabilities = true
            ),
            ambientState = AmbientState.Interactive,

        )
    }
}

@WearPreviewDevices
@Composable
fun PreparingExerciseScreenPreviewAmbient() {
    ThemePreview {
        PreparingExerciseScreen(
            uiState = PreparingScreenState.Preparing(
                serviceState = ServiceState.Connected(
                    ExerciseServiceState()
                ),
                isTrackingInAnotherApp = false,
                requiredPermissions = PreparingViewModel.permissions,
                hasExerciseCapabilities = true
            ),
            ambientState = AmbientState.Ambient()
        )
    }
}
 */
@Composable
fun CaloriesDashboard(
    stats: List<WatchCaloriesStat>,
    ambient: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calories (7 days)",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (ambient) Color.Gray else AccentLime
        )

        Spacer(Modifier.height(6.dp))

        if (stats.isEmpty()) {
            Text("Syncing…", fontSize = 11.sp, color = TextSecondary)
            return
        }

        CaloriesSmoothChart(
            stats = stats,
            ambient = ambient,
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
        )

        Spacer(Modifier.height(6.dp))

        val today = stats.last()

        Text("Today", fontSize = 11.sp, color = TextSecondary)
        Text(
            text = "${today.eaten.toInt()} kcal",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun CaloriesSmoothChart(
    stats: List<WatchCaloriesStat>,
    ambient: Boolean,
    modifier: Modifier = Modifier
) {
    val max = stats.maxOf { max(it.eaten, it.burned) } * 1.2f

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stepX = w / (stats.size - 1)
        val scaleY = h / max

        fun y(v: Float) = h - v * scaleY

        // --- baseline ---
        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(0f, h),
            end = Offset(w, h),
            strokeWidth = 1.dp.toPx()
        )

        // --- eaten smooth path ---
        val eatenPath = Path()
        val eatenPoints = stats.mapIndexed { i, s ->
            Offset(i * stepX, y(s.eaten))
        }

        eatenPath.moveTo(eatenPoints.first().x, eatenPoints.first().y)

        for (i in 0 until eatenPoints.size - 1) {
            val p0 = eatenPoints[i]
            val p1 = eatenPoints[i + 1]
            val cx = (p0.x + p1.x) / 2f

            eatenPath.cubicTo(
                cx, p0.y,
                cx, p1.y,
                p1.x, p1.y
            )
        }

        // --- gradient fill ---
        if (!ambient) {
            val fillPath = Path().apply {
                addPath(eatenPath)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(
                        AccentLime.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )
        }

        // --- eaten line ---
        drawPath(
            path = eatenPath,
            color = if (ambient) Color.LightGray else AccentLime,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // --- burned (thin & subtle) ---
        val burnedPath = Path()
        stats.forEachIndexed { i, s ->
            val x = i * stepX
            val yy = y(s.burned)
            if (i == 0) burnedPath.moveTo(x, yy) else burnedPath.lineTo(x, yy)
        }

        drawPath(
            path = burnedPath,
            color = Color.White.copy(alpha = if (ambient) 0.25f else 0.45f),
            style = Stroke(
                width = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // --- highlight today dot ---
        val today = eatenPoints.last()
        drawCircle(
            color = AccentLime,
            radius = 4.dp.toPx(),
            center = today
        )
    }
}
