package com.example.waterly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.waterly.R

// 1. Fonts setup
val ShrikhandFont = FontFamily(
    Font(R.font.shrikhand_regular)
)

val QuicksandFont = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semi_bold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

// 2. Typography setup
val WaterlyTypography = Typography(

    // Big title (Shrikhand) - App name
    displayLarge = TextStyle(
        fontFamily = ShrikhandFont,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp
    ),

    // Medium title (Shrikhand)
    headlineMedium = TextStyle(
        fontFamily = ShrikhandFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),

    // Body Normal (Quicksand Regular)
    bodyMedium = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    // Body Light (Quicksand Light)
    bodySmall = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),

    // Body Medium (Quicksand Medium)
    bodyLarge = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    // Label SemiBold (Quicksand SemiBold)
    labelMedium = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),

    // Label Bold (Quicksand Bold)
    labelLarge = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),

    // Tiny text (Quicksand Medium for hints, buttons)
    labelSmall = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),

    titleMedium = TextStyle(
        fontFamily = QuicksandFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    )
)
