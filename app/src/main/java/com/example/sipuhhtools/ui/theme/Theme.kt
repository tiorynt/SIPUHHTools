package com.example.sipuhhtools.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BrownDarkPrimary,
    onPrimary = BrownDarkOnPrimary,
    primaryContainer = BrownDarkPrimaryContainer,
    onPrimaryContainer = BrownDarkOnPrimaryContainer,
    background = DarkBackground,
    surface = DarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = BrownOnPrimary,
    primaryContainer = BrownPrimaryContainer,
    onPrimaryContainer = BrownOnPrimaryContainer,
    background = CreamBackground,
    surface = CreamSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = OutlineBrown
)

@Composable
fun SIPUHHToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
