package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TechCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = TechCyanDark,
    onPrimaryContainer = TechWhite,
    secondary = TechAmber,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF4E3800),
    onSecondaryContainer = TechWhite,
    tertiary = TechEmerald,
    onTertiary = Color(0xFF003822),
    background = BlueprintDark,
    onBackground = TechWhite,
    surface = BlueprintSurface,
    onSurface = TechWhite,
    surfaceVariant = BlueprintSurfaceVariant,
    onSurfaceVariant = TechGrayLight,
    outline = BlueprintBorder,
    error = TechCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TechLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF002022),
    secondary = TechAmberDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFF332000),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    background = TechLightBg,
    onBackground = Color(0xFF0F172A),
    surface = TechLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = TechLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = TechCrimson,
    onError = Color.White
)

@Composable
fun BuildWhatYouSeeTheme(
    darkTheme: Boolean = true, // Default to sleek tech engineering blueprint dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
