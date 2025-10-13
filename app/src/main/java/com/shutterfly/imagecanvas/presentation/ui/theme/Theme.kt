package com.shutterfly.imagecanvas.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Grey,
    onPrimary = PrimaryDark,
    background = PrimaryBackground,
    onBackground = White,
    surface = PrimaryBackground ,
    onSurface = White,
    outline = DarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = White,
    background = White,
    onBackground = PrimaryDark,
    surface = White,
    onSurface = PrimaryDark,
    outline = Outline
)

@Composable
fun ImageCanvasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}