package cl.duoc.senalsegura.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealDark,
    secondary = AmberAccent,
    error = AlertRed,
    background = Color(0xFFF7FAFA),
    surface = Color.White
)

private val HighContrastColors = darkColorScheme(
    primary = Color(0xFF72F6F8),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00494A),
    onPrimaryContainer = Color.White,
    secondary = Color.Yellow,
    onSecondary = Color.Black,
    error = Color(0xFFFFB4AB),
    background = Color.Black,
    surface = Color(0xFF111111),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun SenalSeguraTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (highContrast) HighContrastColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

