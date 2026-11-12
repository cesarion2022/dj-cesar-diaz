package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SalsaPink,
    onPrimary = NightBackground,
    primaryContainer = SalsaPurple,
    onPrimaryContainer = TextPrimary,
    secondary = SalsaGold,
    onSecondary = NightBackground,
    secondaryContainer = NightSurfaceElevated,
    onSecondaryContainer = SalsaGold,
    tertiary = SalsaCyan,
    onTertiary = NightBackground,
    background = NightBackground,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NightBorder,
    outlineVariant = NightBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Preserve brand DJ identity
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
