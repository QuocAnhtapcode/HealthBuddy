package com.example.healthbuddy.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.wear.compose.material3.AppScaffold
import com.example.healthbuddy.data.ServiceState
import com.example.healthbuddy.presentation.exercise.ExerciseScreen
import com.example.healthbuddy.presentation.exercise.ExerciseScreenState
import com.example.healthbuddy.service.ExerciseServiceState
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.ambient.LocalAmbientState
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class ExerciseScreenTest(
    override val device: WearDevice
) : WearDeviceScreenshotTest(device) {
    override fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}${
            if (testInfo.methodName.startsWith("active")) {
                ""
            } else {
                "_" +
                    testInfo.methodName.substringBefore(
                        "["
                    )
            }
        }_${device.id}$suffix.png"

    @Test
    fun active() =
        runTest {
            AppScaffold(
                timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) }
            ) {
                ExerciseScreen(
                    ambientState = AmbientState.Interactive,
                    onPauseClick = {},
                    onEndClick = {},
                    onResumeClick = {},
                    onStartClick = {},
                    uiState =
                    ExerciseScreenState(
                        hasExerciseCapabilities = true,
                        isTrackingAnotherExercise = false,
                        serviceState =
                        ServiceState.Connected(
                            ExerciseServiceState()
                        ),
                        exerciseState = ExerciseServiceState()
                    ),
                )
            }
        }

    @Test
    fun ambient() =
        runTest {
            // Only run for one variant
            Assume.assumeTrue(device == WearDevice.GooglePixelWatch)

            CompositionLocalProvider(LocalAmbientState provides AmbientState.Ambient()) {
                AppScaffold(
                    timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) }
                ) {
                    ExerciseScreen(
                        onPauseClick = {},
                        onEndClick = {},
                        onResumeClick = {},
                        onStartClick = {},
                        uiState =
                        ExerciseScreenState(
                            hasExerciseCapabilities = true,
                            isTrackingAnotherExercise = false,
                            serviceState =
                            ServiceState.Connected(
                                ExerciseServiceState()
                            ),
                            exerciseState = ExerciseServiceState()
                        ),
                        ambientState = AmbientState.Ambient()
                    )
                }
            }
        }
}
