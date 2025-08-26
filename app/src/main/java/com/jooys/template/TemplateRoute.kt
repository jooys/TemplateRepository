package com.jooys.template

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jooys.template.core_design.component.compose.CustomSnackBar
import com.jooys.template.core_design.component.compose.SnackBarHandler
import com.jooys.template.core_design.theme.CustomTheme
import com.jooys.template.core_design.theme.BLUE
import com.jooys.template.core_design.util.singleClickable
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewmodel.compose.viewModel as ComposableViewModel

@Composable
fun TemplateRoute(
    viewModel: TemplateViewModel = ComposableViewModel(),
) {
    val context = LocalContext.current
    val title by viewModel.state.map { it.title }.collectAsStateWithLifecycle("")
    val snackBarHandler = remember { SnackBarHandler() }
    val snackBarState by snackBarHandler.snackBarState.collectAsStateWithLifecycle()


    CustomTheme {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            TemplateScreen(
                title = title,
                onClickButton = {
                    viewModel.onAction(TemplateAction.OnClickButton)
                }
            )
            CustomSnackBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                message = snackBarState.message,
                visible = snackBarState.visible,
                onClick = { snackBarHandler.dismiss() }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(TemplateAction.OnViewCreated)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                TemplateMutate.SideEffect.Finish -> {
                    (context as Activity).finish()
                }

                is TemplateMutate.SideEffect.ShowSnackBar -> {
                    snackBarHandler.show(sideEffect.message)
                }
            }
        }
    }
}

@Composable
internal fun TemplateScreen(
    title: String,
    onClickButton: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = title,
            style = CustomTheme.typography.body1
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("bannerUrl")
                .crossfade(100)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp)
        )
        Text(
            modifier = Modifier
                .size(100.dp, 50.dp)
                .background(color = BLUE)
                .wrapContentHeight()
                .singleClickable(onClick = onClickButton),
            text = "버튼",
            style = CustomTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewTemplateContent() {
    TemplateScreen(
        "",
        onClickButton = {})
}
