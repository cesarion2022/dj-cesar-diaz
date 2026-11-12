package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeatherInfo
import com.example.ui.theme.NightBorder
import com.example.ui.theme.NightSurface
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WeatherClockHeader(
    currentTime: String,
    currentDate: String,
    weather: WeatherInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF140D26),
                        Color(0xFF1F1230),
                        Color(0xFF0F1524)
                    )
                )
            )
            .border(1.dp, NightBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("weather_clock_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clock & Date Section
            Column(
                modifier = Modifier.weight(1.1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "🕐",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "COLOMBIA",
                        color = SalsaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                Text(
                    text = currentTime,
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = currentDate,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            // Weather Section
            Row(
                modifier = Modifier.weight(0.9f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.iconEmoji,
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${weather.temp}°C",
                        color = SalsaGold,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = weather.description,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )

                    Text(
                        text = weather.location,
                        color = SalsaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
