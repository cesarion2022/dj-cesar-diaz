package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SoundEffect
import com.example.ui.theme.NightBackground
import com.example.ui.theme.NightBorder
import com.example.ui.theme.NightBorderSubtle
import com.example.ui.theme.NightSurface
import com.example.ui.theme.NightSurfaceElevated
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PercussionSoundboard(
    onPlayEffect: (SoundEffect) -> Unit,
    bpm: Int?,
    bpmCategory: String?,
    onTapBpm: () -> Unit,
    onResetBpm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun performHaptic() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Column {
            Text(
                text = "CABINA DJ • EFECTOS & RITMO",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "Percusión salsa en tiempo real y detector de BPM",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Soundboard Grid (6 effects: Clave, Campana, Güiro, Horn, Azúcar, Conga)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val effects = SoundEffect.values()
            for (row in 0 until 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        if (index < effects.size) {
                            val effect = effects[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(NightSurface)
                                    .border(1.dp, NightBorderSubtle, RoundedCornerShape(16.dp))
                                    .clickable {
                                        performHaptic()
                                        onPlayEffect(effect)
                                    }
                                    .padding(vertical = 14.dp, horizontal = 8.dp)
                                    .testTag("sound_pad_${effect.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = effect.emoji,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = effect.title,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = effect.subtitle,
                                        color = SalsaGold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // BPM Tap Salsa Counter Tool
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NightSurfaceElevated)
                .border(1.dp, NightBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "METRÓNOMO & BPM SALSA",
                        color = SalsaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (bpm != null) "$bpm BPM" else "Toca al compás",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = bpmCategory ?: "Sincroniza el paso de salsa (1-2-3... 5-6-7)",
                        color = if (bpm != null) SalsaGold else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (bpm != null) {
                        IconButton(
                            onClick = onResetBpm,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reiniciar BPM",
                                tint = TextSecondary
                            )
                        }
                    }

                    // Big Tap Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SalsaGold, SalsaPink)
                                )
                            )
                            .clickable {
                                performHaptic()
                                onTapBpm()
                            }
                            .testTag("bpm_tap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TAP",
                            color = NightBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
