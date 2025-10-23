package com.example.healthbuddy.presentation

import androidx.wear.compose.material3.AppScaffold
import com.example.healthbuddy.presentation.summary.SummaryScreen
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import java.time.Duration
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class SummaryScreenTest(
    override val device: WearDevice
) : WearDeviceScreenshotTest(device) {
    @Test
    fun summary() {
        runTest {
            AppScaffold(
                timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) }
            ) {
                SummaryScreen(
                    uiState =
                    SummaryScreenState(
                        averageHeartRate = 75.0,
                        totalDistance = 2000.0,
                        totalCalories = 100.0,
                        elapsedTime = Duration.ofMinutes(17).plusSeconds(1)
                    ),
                    onRestartClick = {}
                )
            }
        }

        // TODO reinstate swipe tests after robolectric/compose fix
//        composeRule.onNode(hasScrollToIndexAction())
//            .performTouchInput {
//                repeat(10) {
//                    swipeUp()
//                }
//            }
//
//        captureScreenshot("_end")
    }
}
