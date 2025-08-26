package com.jooys.template.core_design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val colorScheme = lightColorScheme(
    primary = BLACK,
    onPrimary = Color.Black,
    background = Color.White,
    secondary = BLUE,
)

@Composable
fun CustomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = colorScheme) {
        ProvideTextStyle(value = textStyle) {
            content()
        }
    }
}

object CustomTheme {
    val typography: CustomTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}
