package com.example.healthbuddy.presentation.ambient

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import com.google.android.horologist.compose.ambient.AmbientState

private val grayscale =
    Paint().apply {
        colorFilter =
            ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(0f)
                }
            )
        isAntiAlias = false
    }

fun Modifier.ambientGray(ambientState: AmbientState): Modifier =
    graphicsLayer {
        if (ambientState.isAmbient) {
            scaleX = 0.9f
            scaleY = 0.9f
        }
    }.drawWithContent {
        if (ambientState.isAmbient) {
            drawIntoCanvas {
                it.withSaveLayer(size.toRect(), grayscale) {
                    drawContent()
                }
            }
        } else {
            drawContent()
        }
    }

fun Modifier.ambientBlank(ambientState: AmbientState): Modifier =
    drawWithContent {
        if (ambientState.isInteractive) {
            drawContent()
        }
    }
