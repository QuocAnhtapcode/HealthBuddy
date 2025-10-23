package com.example.healthbuddy.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import com.example.healthbuddy.data.ServiceState
import com.example.healthbuddy.presentation.preparing.PreparingExerciseScreen
import com.example.healthbuddy.presentation.preparing.PreparingScreenState
import com.example.healthbuddy.presentation.preparing.PreparingViewModel
import com.example.healthbuddy.service.ExerciseServiceState
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class PreparingExerciseScreenTest(
    override val device: WearDevice
) : WearDeviceScreenshotTest(device) {
    override fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}${
            if (testInfo.methodName.startsWith("preparing")) {
                ""
            } else {
                "_" +
                    testInfo.methodName.substringBefore(
                        "["
                    )
            }
        }_${device.id}$suffix.png"

    @Composable
   override fun TestScaffold(content: @Composable () -> Unit) {
        CorrectLayout {
            AppScaffold(timeText = {}) {
                content()
            }
        }
    }


    @Test
    fun preparing() =
        runTest {
            AppScaffold {
                    PreparingExerciseScreen(
                        uiState =
                            PreparingScreenState.Preparing(
                                serviceState =
                                    ServiceState.Connected(
                                        ExerciseServiceState()
                                    ),
                                isTrackingInAnotherApp = false,
                                requiredPermissions = PreparingViewModel.permissions,
                                hasExerciseCapabilities = true
                            ),
                        ambientState = AmbientState.Interactive
                    )
                }
        }

    @Test
    fun ambient() =
        runTest {
            // Only run for one variant
            Assume.assumeTrue(device == WearDevice.GooglePixelWatch)

            AppScaffold {
                PreparingExerciseScreen(
                    uiState =
                    PreparingScreenState.Preparing(
                        serviceState =
                        ServiceState.Connected(
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
}
