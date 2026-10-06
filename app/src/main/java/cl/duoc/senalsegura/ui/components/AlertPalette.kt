package cl.duoc.senalsegura.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AlertPaletteColors(
    val dominant: Color,
    val vibrant: Color,
    val muted: Color
) {
    val onDominant: Color
        get() = readableTextColor(dominant)

    val onVibrant: Color
        get() = readableTextColor(vibrant)
}

@Composable
fun rememberAlertPaletteColors(
    @DrawableRes drawableResource: Int,
    fallbackDominant: Color,
    fallbackVibrant: Color,
    fallbackMuted: Color
): AlertPaletteColors {
    val context = LocalContext.current
    val fallback = AlertPaletteColors(
        dominant = fallbackDominant,
        vibrant = fallbackVibrant,
        muted = fallbackMuted
    )

    val paletteColors by produceState(
        initialValue = fallback,
        drawableResource,
        fallbackDominant,
        fallbackVibrant,
        fallbackMuted
    ) {
        value = withContext(Dispatchers.Default) {
            val drawable = ContextCompat.getDrawable(context, drawableResource)
                ?: return@withContext fallback
            val width = drawable.intrinsicWidth.coerceAtLeast(1)
            val height = drawable.intrinsicHeight.coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            drawable.setBounds(0, 0, width, height)
            drawable.draw(Canvas(bitmap))

            val palette = Palette.from(bitmap).generate()
            AlertPaletteColors(
                dominant = Color(palette.getDominantColor(fallbackDominant.toArgb())),
                vibrant = Color(palette.getVibrantColor(fallbackVibrant.toArgb())),
                muted = Color(palette.getMutedColor(fallbackMuted.toArgb()))
            )
        }
    }

    return paletteColors
}

private fun readableTextColor(background: Color): Color {
    return if (background.luminance() < 0.45f) Color.White else Color.Black
}
