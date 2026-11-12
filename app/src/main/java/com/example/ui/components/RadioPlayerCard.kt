package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RadioStation
import com.example.ui.theme.LiveRed
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
fun RadioPlayerCard(
    stations: List<RadioStation>,
    selectedIndex: Int,
    isPlaying: Boolean,
    isBuffering: Boolean,
    volume: Float,
    errorMessage: String?,
    onSelectStation: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStation = stations.getOrNull(selectedIndex) ?: stations[0]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(NightSurface)
            .border(1.dp, NightBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("radio_player_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header row with Live Badge & Station Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Radio en Vivo" title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RADIO EN VIVO",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Live status tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isPlaying) LiveRed.copy(alpha = 0.2f) else NightSurfaceElevated)
                        .border(
                            1.dp,
                            if (isPlaying) LiveRed else NightBorderSubtle,
                            RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) LiveRed else SalsaGold)
                    )
                    Text(
                        text = if (isBuffering) "CONECTANDO..." else if (isPlaying) "24/7 EN VIVO" else "EN PAUSA",
                        color = if (isPlaying) LiveRed else SalsaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Station Selection Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stations.forEachIndexed { index, station ->
                    val isSelected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(SalsaPink, SalsaGold))
                                } else {
                                    Brush.linearGradient(listOf(NightSurfaceElevated, NightSurfaceElevated))
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else NightBorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectStation(index) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                            .testTag("station_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = station.name,
                            color = if (isSelected) NightBackground else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            // Turntable and Station Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                TurntableDisc(
                    isPlaying = isPlaying,
                    size = 118.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = currentStation.name,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = currentStation.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DJ César Díaz • Ibagué",
                        color = SalsaCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Animated Spectrum Visualizer
            AudioSpectrum(
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxWidth()
            )

            // Playback and Volume Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Big Glowing Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(12.dp, CircleShape, spotColor = SalsaPink, ambientColor = SalsaPink)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(SalsaPink, SalsaGold)
                            )
                        )
                        .clickable { onTogglePlayPause() }
                        .testTag("radio_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = NightBackground,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            tint = NightBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Volume slider section
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { onVolumeChange(if (volume > 0f) 0f else 0.8f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                volume <= 0.05f -> Icons.Default.VolumeMute
                                volume < 0.5f -> Icons.Default.VolumeDown
                                else -> Icons.Default.VolumeUp
                            },
                            contentDescription = "Volumen",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Slider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = SalsaGold,
                            activeTrackColor = SalsaGold,
                            inactiveTrackColor = NightSurfaceElevated
                        ),
                        modifier = Modifier.weight(1f).testTag("volume_slider")
                    )

                    Text(
                        text = "${(volume * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(34.dp)
                    )
                }
            }

            // Error notice if any
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FF1744))
                            .border(1.dp, Color(0x66FF1744), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
