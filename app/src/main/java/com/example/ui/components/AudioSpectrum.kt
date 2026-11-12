package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AudioSpectrum(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 36
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SpectrumAnimation")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpectrumProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D0716))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val totalSpacing = barCount * 2f
            val barWidth = ((canvasWidth - totalSpacing) / barCount).coerceAtLeast(4f)
            val baseRadius = CornerRadius(4f, 4f)

            for (i in 0 until barCount) {
                val x = i * (barWidth + 2f) + 2f

                // Color gradient cycling per bar
                val barGradient = when (i % 3) {
                    0 -> Brush.verticalGradient(
                        colors = listOf(SalsaPink, SalsaPink.copy(alpha = 0.35f))
                    )
                    1 -> Brush.verticalGradient(
                        colors = listOf(SalsaGold, SalsaGold.copy(alpha = 0.35f))
                    )
                    else -> Brush.verticalGradient(
                        colors = listOf(SalsaCyan, SalsaCyan.copy(alpha = 0.35f))
                    )
                }

                val barHeight = if (isPlaying) {
                    // Dynamic frequency wave calculation
                    val wave1 = abs(sin(animProgress * 0.8f + i * 0.45f))
                    val wave2 = abs(sin(animProgress * 1.5f + i * 0.85f))
                    val centerBoost = 1f - abs((i - barCount / 2f) / (barCount / 2f)) * 0.3f
                    val normalized = (wave1 * 0.6f + wave2 * 0.4f) * centerBoost
                    (normalized * (canvasHeight - 16.dp.toPx()) + 8.dp.toPx()).coerceIn(
                        6.dp.toPx(),
                        canvasHeight - 8.dp.toPx()
                    )
                } else {
                    // Gentle ambient idle pulse
                    val idle = abs(sin(animProgress * 0.25f + i * 0.3f))
                    (idle * 12.dp.toPx() + 6.dp.toPx()).coerceAtMost(24.dp.toPx())
                }

                val y = canvasHeight - barHeight - 4.dp.toPx()

                drawRoundRect(
                    brush = barGradient,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = baseRadius
                )
            }
        }
    }
}
