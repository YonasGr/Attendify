package com.attendify.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF), // Indigo-100
    onPrimaryContainer = Color(0xFF312E81), // Indigo-900
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5), // Emerald-100
    onSecondaryContainer = Color(0xFF065F46), // Emerald-800
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE9FE), // Violet-100
    onTertiaryContainer = Color(0xFF5B21B6), // Violet-800
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9), // Slate-100
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2), // Red-100
    onErrorContainer = Color(0xFF991B1B) // Red-800
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF312E81), // Indigo-900
    primaryContainer = DarkPrimaryVariant,
    onPrimaryContainer = Color(0xFFE0E7FF), // Indigo-100
    secondary = DarkSecondary,
    onSecondary = Color(0xFF065F46), // Emerald-800
    secondaryContainer = Color(0xFF047857), // Emerald-700
    onSecondaryContainer = Color(0xFFD1FAE5), // Emerald-100
    tertiary = DarkTertiary,
    onTertiary = Color(0xFF5B21B6), // Violet-800
    tertiaryContainer = Color(0xFF7C3AED), // Violet-600
    onTertiaryContainer = Color(0xFFEDE9FE), // Violet-100
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = Color(0xFF7F1D1D), // Red-900
    errorContainer = Color(0xFFDC2626), // Red-600
    onErrorContainer = Color(0xFFFEE2E2) // Red-100
)

@Composable
fun AttendifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
