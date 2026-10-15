package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ToofanGreen,
    onPrimary = ToofanWhite,
    primaryContainer = ToofanGreen.copy(alpha = 0.12f),
    onPrimaryContainer = ToofanMainDark,
    secondary = ToofanLinkPink,
    onSecondary = ToofanWhite,
    secondaryContainer = ToofanLinkPink.copy(alpha = 0.12f),
    onSecondaryContainer = ToofanMainDark,
    tertiary = ToofanMainDark,
    onTertiary = ToofanWhite,
    background = ToofanBgColor,
    onBackground = ToofanMainDark,
    surface = ToofanWhite,
    onSurface = ToofanMainDark,
    surfaceVariant = ToofanWhite,
    onSurfaceVariant = ToofanBodyText,
    outline = ToofanGrey1,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = ToofanGreen,
    onPrimary = ToofanWhite,
    primaryContainer = ToofanGreen.copy(alpha = 0.2f),
    onPrimaryContainer = DarkTextPrimary,
    secondary = ToofanLinkPink,
    onSecondary = ToofanWhite,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = ToofanGrey1.copy(alpha = 0.3f),
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
