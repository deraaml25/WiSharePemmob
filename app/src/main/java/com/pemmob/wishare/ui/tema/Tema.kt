package com.pemmob.wishare.ui.tema

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

private val SkemaWarnaGelap = darkColorScheme(
    primary = BiruUtamaGelap,
    secondary = BiruNeon,
    background = LatarGelap,
    surface = PermukaanGelap,
    surfaceVariant = PermukaanGelapVarian,
    onPrimary = LatarGelap,
    onBackground = TeksGelap,
    onSurface = TeksGelap,
    onSurfaceVariant = TeksSekunderGelap,
    error = MerahBahaya
)

private val SkemaWarnaTerang = lightColorScheme(
    primary = BiruUtama,
    secondary = BiruUtamaGelap,
    background = LatarTerang,
    surface = PermukaanTerang,
    surfaceVariant = PermukaanTerangVarian,
    onPrimary = PermukaanTerang,
    onBackground = TeksTerang,
    onSurface = TeksTerang,
    onSurfaceVariant = TeksSekunderTerang,
    error = MerahBahaya
)

@Composable
fun TemaWiShare(
    gelapTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color tersedia di Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val skemaWarna = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (gelapTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        gelapTheme -> SkemaWarnaGelap
        else -> SkemaWarnaTerang
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = skemaWarna.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !gelapTheme
        }
    }

    MaterialTheme(
        colorScheme = skemaWarna,
        content = content
    )
}
