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
    primary = TropiPrimaryDark,
    onPrimary = TropiOnPrimaryDark,
    primaryContainer = TropiPrimaryContainerDark,
    onPrimaryContainer = TropiOnPrimaryContainerDark,
    secondary = TropiSecondaryDark,
    onSecondary = TropiOnSecondaryDark,
    tertiary = TropicalOrange,
    onTertiary = Color.White,
    background = TropiBackgroundDark,
    onBackground = TropiOnBackgroundDark,
    surface = TropiSurfaceDark,
    onSurface = TropiOnSurfaceDark,
    surfaceVariant = Color(0xFF1E2A23),
    onSurfaceVariant = Color(0xFFBCCBC1)
)

private val LightColorScheme = lightColorScheme(
    primary = TropiPrimaryLight,
    onPrimary = TropiOnPrimaryLight,
    primaryContainer = TropiPrimaryContainerLight,
    onPrimaryContainer = TropiOnPrimaryContainerLight,
    secondary = TropiSecondaryLight,
    onSecondary = TropiOnSecondaryLight,
    secondaryContainer = TropiSecondaryContainerLight,
    onSecondaryContainer = TropiOnSecondaryContainerLight,
    tertiary = TropiTertiaryLight,
    onTertiary = TropiOnTertiaryLight,
    background = TropiBackgroundLight,
    onBackground = TropiOnBackgroundLight,
    surface = TropiSurfaceLight,
    onSurface = TropiOnSurfaceLight,
    surfaceVariant = TropiSurfaceVariantLight,
    onSurfaceVariant = TropiOnSurfaceVariantLight,
    outline = Color(0xFF6E8177)
)

@Composable
fun TropiVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve TropiVault's signature tropical branding
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
