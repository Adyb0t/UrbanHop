package com.example.urbanhop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.urbanhop.utils.AppGradients

private val gradients = AppGradients(
    background = Brush.radialGradient(
        colors = listOf(
            Color(0xFF0B1018),
            Color(0xFF0B1018),
            Color(0xFF0B1018),
            Color(0xFF0B1018),
            Color(0xFF0B1018),
            Color(0xFF0B1018),
            TransitBlue.copy(0.25f)
        ),
        radius = 900f
    )
)

enum class LineColor(
    val code: String,
    val color: Color
) {
    KJ(
        code = "KJ",
        color = KJRed
    ),
    AG(
        code = "AG",
        color = AGOrange
    ),
    SP(
        code = "SP",
        color = SPMaroon
    ),
    KG(
        code = "KG",
        color = KGGreen
    ),
    PY(
        code = "PY",
        color = PYYellow
    ),
    MR(
        code = "MR",
        color = MRLime
    )
}

val LocalAppGradients = staticCompositionLocalOf<AppGradients> {
    error("No gradients provided")
}
val LocalLineColors = staticCompositionLocalOf<Array<LineColor>> {
    error("No line colors provided")
}
private val DarkColorScheme = darkColorScheme(

    primary = TransitBlue,
    onPrimary = onTransitBlue,

    primaryContainer = DarkBlue,
    onPrimaryContainer = BluishWhite,

    secondary = Secondary,
    onSecondary = onTransitBlue,

    tertiary = AltWhite,
    onTertiary = onTransitBlue,

    surface = DarKSchemeSurface,
    onSurface = BluishWhite,

    background = DarkerSurface,
    onBackground = AltWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    //focus on dark theme only
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UrbanHopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val appGradients = gradients
    val lineColorSet = LineColor.entries.toTypedArray()


    CompositionLocalProvider(
        LocalAppGradients provides appGradients,
        LocalLineColors provides lineColorSet
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}