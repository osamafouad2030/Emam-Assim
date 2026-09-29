package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val MishkahDarkColorScheme = darkColorScheme(
    primary = MishkahGoldPrimary,
    onPrimary = Color(0xFF141105),
    primaryContainer = Color(0xFF3B3011),
    onPrimaryContainer = MishkahGoldLight,
    secondary = MishkahEmeraldLight,
    onSecondary = Color(0xFF042117),
    secondaryContainer = MishkahEmeraldDeep,
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = MishkahGoldLight,
    onTertiary = Color(0xFF1A1405),
    background = MishkahObsidian,
    onBackground = MishkahIvoryText,
    surface = MishkahObsidianSurface,
    onSurface = MishkahIvoryText,
    surfaceVariant = MishkahObsidianElevated,
    onSurfaceVariant = MishkahMutedSand,
    outline = Color(0xFF3E4750),
    error = AlertHighCoral,
    onError = Color.White
)

private val MishkahLightColorScheme = lightColorScheme(
    primary = MishkahEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F5E8),
    onPrimaryContainer = MishkahEmeraldDeep,
    secondary = MishkahGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAEAB9),
    onSecondaryContainer = Color(0xFF3B2B04),
    tertiary = MishkahEmeraldLight,
    onTertiary = Color.White,
    background = MishkahParchmentBg,
    onBackground = MishkahInkDark,
    surface = MishkahParchmentSurface,
    onSurface = MishkahInkDark,
    surfaceVariant = MishkahParchmentVariant,
    onSurfaceVariant = MishkahInkMuted,
    outline = Color(0xFFD2C7B2),
    error = AlertHighCoral,
    onError = Color.White
)

val MishkahShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MishkahDarkColorScheme else MishkahLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MishkahShapes,
        content = content
    )
}
