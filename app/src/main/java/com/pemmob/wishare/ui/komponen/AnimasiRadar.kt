package com.pemmob.wishare.ui.komponen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AnimasiRadar(
    isMencari: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radiusAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val warnaUtama = MaterialTheme.colorScheme.primary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = size.minDimension / 2f
            
            // Lingkaran statis (dasar radar)
            drawCircle(
                color = warnaUtama.copy(alpha = 0.1f),
                radius = maxRadius * 0.33f,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = warnaUtama.copy(alpha = 0.05f),
                radius = maxRadius * 0.66f,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = warnaUtama.copy(alpha = 0.02f),
                radius = maxRadius,
                style = Stroke(width = 1.dp.toPx())
            )

            if (isMencari) {
                // Lingkaran yang membesar (animasi)
                drawCircle(
                    color = warnaUtama.copy(alpha = alphaAnim * 0.3f),
                    radius = maxRadius * radiusAnim
                )
                drawCircle(
                    color = warnaUtama.copy(alpha = alphaAnim * 0.8f),
                    radius = maxRadius * radiusAnim,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
