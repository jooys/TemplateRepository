package com.jooys.template.core_design.component.compose

import android.animation.ValueAnimator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.jooys.template.core_design.theme.BLACK

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomLoading(
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.apply {
            window.setDimAmount(0f)
            LaunchedEffect(Unit) {
                ValueAnimator.ofFloat(0f, 0.4f)
                    .setDuration(300L)
                    .apply {
                        startDelay = 300L
                        addUpdateListener { window.setDimAmount(it.animatedValue as Float) }
                    }
                    .start()
            }
        }
        LoadingIndicator()
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.size(48.dp)
        ) {
            CircularProgressIndicator(
                color = BLACK,
                strokeWidth = 2.dp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingIndicator() {
    LoadingIndicator()
}
