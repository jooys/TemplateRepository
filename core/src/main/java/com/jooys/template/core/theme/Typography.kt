package com.jooys.template.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val textStyle = TextStyle().copy(
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    ),
)
data class CustomTypography(
    /**
     * h1
     * @param color COOL_GRAY_900
     * @param fontSize 32.sp
     */
    val h1: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 32.sp,
    ),

    /**
     * h2
     * @param color COOL_GRAY_900
     * @param fontSize 26.sp
     */
    val h2: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 26.sp,
    ),

    /**
     * title1
     * @param color COOL_GRAY_900
     * @param fontSize 22.sp
     */
    val title1: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 22.sp,
    ),

    /**
     * title2
     * @param color COOL_GRAY_900
     * @param fontSize 20.sp
     */
    val title2: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 20.sp,
    ),

    /**
     * title3
     * @param color COOL_GRAY_900
     * @param fontSize 18.sp
     */
    val title3: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 18.sp,
    ),

    /**
     * title4
     * @param color COOL_GRAY_900
     * @param fontSize 16.sp
     */
    val title4: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 16.sp,
    ),

    /**
     * title5
     * @param color COOL_GRAY_900
     * @param fontSize 14.sp
     */
    val title5: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 14.sp,
    ),

    /**
     * body1
     * @param color COOL_GRAY_900
     * @param fontSize 16.sp
     */
    val body1: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 16.sp,
    ),

    /**
     * body2
     * @param color COOL_GRAY_900
     * @param fontSize 14.sp
     */
    val body2: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 14.sp,
    ),

    /**
     * body3
     * @param color COOL_GRAY_900
     * @param fontSize 13.sp
     */
    val body3: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 13.sp,
    ),

    /**
     * caption1
     * @param color COOL_GRAY_900
     * @param fontSize 13.sp
     */
    val caption1: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 13.sp,
    ),

    /**
     * caption2
     * @param color COOL_GRAY_900
     * @param fontSize 12.sp
     */
    val caption2: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 12.sp,
    ),

    /**
     * tag1
     * @param color COOL_GRAY_900
     * @param fontSize 10.sp
     */
    val tag1: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 10.sp,
    ),

    /**
     * tag2
     * @param color COOL_GRAY_900
     * @param fontSize 10.sp
     */
    val tag2: TextStyle = textStyle.copy(
        color = BLACK,
        fontSize = 10.sp,
    )
)

internal val LocalTypography = staticCompositionLocalOf { CustomTypography() }
