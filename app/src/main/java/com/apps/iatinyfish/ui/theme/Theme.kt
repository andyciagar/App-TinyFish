package com.apps.iatinyfish.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Ocean,
    onPrimary = Color.White,
    primaryContainer = SeaFoam,
    onPrimaryContainer = OceanDark,
    secondary = Color(0xFF166582),
    secondaryContainer = Color(0xFFE0F5FC),
    tertiary = Color(0xFF0F766E),
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEAF4F7),
    onSurfaceVariant = Color(0xFF52616B),
    outlineVariant = Color(0xFFC9D9DE),
)

private val DarkColorScheme = darkColorScheme(
    primary = Aqua,
    onPrimary = Night,
    primaryContainer = OceanDark,
    onPrimaryContainer = SeaFoam,
    secondary = Aqua,
    background = Night,
    onBackground = Color.White,
    surface = NightSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF16384C),
    onSurfaceVariant = Color(0xFF94A3B8),
    outlineVariant = Color(0xFF1E455E),
)

@Composable
fun IATinyFishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
}
