package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val MokayDarkColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = Color.White,
    primaryContainer = BoxyLavenderDeep,
    onPrimaryContainer = WarmParchment,
    secondary = RunicTeal,
    onSecondary = InkBlack,
    secondaryContainer = CardSlate,
    onSecondaryContainer = WarmParchment,
    tertiary = SparkGold,
    onTertiary = InkBlack,
    background = DeepTwilight,
    onBackground = WarmParchment,
    surface = CardSlate,
    onSurface = WarmParchment,
    surfaceVariant = ForestSlate,
    onSurfaceVariant = BoxyLavender,
    error = VitalCrimson,
    onError = Color.White
)

private val MokayLightColorScheme = lightColorScheme(
    primary = FlameOrange,
    onPrimary = Color.White,
    primaryContainer = BoxyLavender,
    onPrimaryContainer = InkBlack,
    secondary = RunicTeal,
    onSecondary = InkBlack,
    tertiary = SparkGold,
    onTertiary = InkBlack,
    background = WarmParchment,
    onBackground = InkBlack,
    surface = ParchmentPaper,
    onSurface = InkBlack,
    error = VitalCrimson,
    onError = Color.White
)

@Composable
fun MokayWorldTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        darkTheme -> MokayDarkColorScheme
        else -> MokayLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MokayTypography,
        content = content
    )
}
