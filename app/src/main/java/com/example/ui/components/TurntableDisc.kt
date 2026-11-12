package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NightBackground
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink

@Composable
fun TurntableDisc(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VinylSpinTransition")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VinylSpinAngle"
    )

    // Needle arm angle: resting when stopped (0f), angled onto record when playing (24f)
    val needleAngle by animateFloatAsState(
        targetValue = if (isPlaying) 22f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "NeedleAngle"
    )

    val currentRotation = if (isPlaying) spinAngle else 0f

    Box(
        modifier = modifier
            .size(size)
            .testTag("turntable_disc_container"),
        contentAlignment = Alignment.Center
    ) {
        // Vinyl Record with glow shadow
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (isPlaying) 16.dp else 6.dp,
                    shape = CircleShape,
                    ambientColor = SalsaPink,
                    spotColor = SalsaPink
                )
                .clip(CircleShape)
                .background(Color(0xFF100C14))
                .rotate(currentRotation),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl surface grooves Canvas
            Canvas(modifier = Modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val maxRadius = this.size.width / 2f

                // Outer border glow
                drawCircle(
                    color = SalsaPink.copy(alpha = if (isPlaying) 0.8f else 0.4f),
                    radius = maxRadius - 2f,
                    style = Stroke(width = 2.5f)
                )

                // Concentric vinyl sound grooves
                val grooveColor = Color(0xFF25202C)
                val highlightGrooveColor = Color(0xFF383244)
                val step = (maxRadius - 38.dp.toPx()) / 8f

                for (i in 1..7) {
                    val r = 38.dp.toPx() + i * step
                    drawCircle(
                        color = if (i % 2 == 0) highlightGrooveColor else grooveColor,
                        radius = r,
                        style = Stroke(width = 1.2f)
                    )
                }

                // Vinyl sheen highlights (angled shimmer across the disc)
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.15f to Color.White.copy(alpha = 0.08f),
                        0.3f to Color.Transparent,
                        0.5f to Color.Transparent,
                        0.65f to Color.White.copy(alpha = 0.08f),
                        0.8f to Color.Transparent,
                        1.0f to Color.Transparent
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true
                )
            }

            // Center Label (Neon Salsa Disc Badge)
            Box(
                modifier = Modifier
                    .size(size * 0.38f)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(SalsaPink, SalsaGold)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Label inner circle
                Box(
                    modifier = Modifier
                        .size(size * 0.34f)
                        .clip(CircleShape)
                        .background(Color(0xFF1E0E28)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SALSA",
                        color = SalsaGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    // Spindle Hole
                    Box(
                        modifier = Modifier
                            .size(size * 0.08f)
                            .clip(CircleShape)
                            .background(NightBackground)
                    )
                }
            }
        }

        // DJ Tone Arm / Needle (Canvas overlay)
        Canvas(
            modifier = Modifier
                .size(size)
                .rotate(needleAngle)
        ) {
            val pivot = Offset(this.size.width * 0.88f, this.size.height * 0.12f)
            val armEnd = Offset(this.size.width * 0.65f, this.size.height * 0.60f)
            val headEnd = Offset(this.size.width * 0.58f, this.size.height * 0.68f)

            // Tone arm pivot base
            drawCircle(
                color = Color(0xFF423752),
                radius = 8.dp.toPx(),
                center = pivot
            )
            drawCircle(
                color = SalsaCyan,
                radius = 4.dp.toPx(),
                center = pivot
            )

            // Metallic tone arm rod
            drawLine(
                color = Color(0xFFE0E0E0),
                start = pivot,
                end = armEnd,
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Cartridge / Needle head
            drawLine(
                color = SalsaCyan,
                start = armEnd,
                end = headEnd,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
