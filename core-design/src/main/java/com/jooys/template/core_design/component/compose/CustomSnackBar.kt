package com.jooys.template.core_design.component.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jooys.template.core_design.theme.BLACK
import com.jooys.template.core_design.theme.CustomTheme

@Composable
fun CustomSnackBar(
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .clickable(onClick = onClick)
    ) {
        Snackbar(
            backgroundColor = BLACK,
            elevation = 0.dp,
            content = {
                Text(
                    text = message,
                    color = Color.White,
                    maxLines = 2,
                    style = CustomTheme.typography.body2
                )
            }
        )
    }
}

