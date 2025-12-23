@file:OptIn(ExperimentalCoilApi::class)

package com.jooys.template.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jooys.template.core.component.compose.CustomLoading
import com.jooys.template.core.component.compose.CustomSnackBar
import com.jooys.template.core.component.compose.SnackBarHandler
import com.jooys.template.core.component.compose.SnackBarState
import com.jooys.template.core.component.compose.ToolbarWithBackButton
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.core.theme.GRAY
import com.jooys.template.core.theme.NEUTRAL_GRAY_50
import com.jooys.template.core.util.findActivity
import com.jooys.template.core.util.roundedCornerShape
import com.jooys.template.core.util.singleClickable
import androidx.lifecycle.viewmodel.compose.viewModel as ComposableViewModel

@Composable
fun DetailRoute(
    viewModel: DetailViewModel = ComposableViewModel(),
) {
    val activity = LocalContext.current.findActivity()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHandler = remember { SnackBarHandler() }
    val snackBarState by snackBarHandler.snackBarState.collectAsStateWithLifecycle()

    DetailScreen(
        viewState = state.viewState,
        imageData = state.imageData,
        clickBackButton = {
            viewModel.onAction(DetailAction.OnClickBack)
        },
        onClickBookmark = {
            viewModel.onAction(DetailAction.OnClickBookmark(state.imageData))
        },
        snackBarHandler = snackBarHandler,
        snackBarState = snackBarState
    )

    if (state.isLoading) {
        CustomLoading()
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(DetailAction.OnViewCreated)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                DetailMutate.SideEffect.Finish -> {
                    activity.finish()
                }

                is DetailMutate.SideEffect.ShowSnackBar -> {
                    snackBarHandler.show(sideEffect.message)
                }
            }
        }
    }
}

@Composable
internal fun DetailScreen(
    viewState: DetailMutate.State.ViewState,
    imageData: DetailMutate.State.ImageData,
    clickBackButton: () -> Unit,
    onClickBookmark: () -> Unit,
    snackBarHandler: SnackBarHandler,
    snackBarState: SnackBarState,
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            ToolbarWithBackButton("이미지 상세", clickBackButton = clickBackButton)
        }
    ) {
        Box(
            modifier = Modifier
                .background(NEUTRAL_GRAY_50)
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                when (viewState) {
                    DetailMutate.State.ViewState.INITIALIZE -> Unit
                    DetailMutate.State.ViewState.SHOW_DATA -> {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .roundedCornerShape(
                                    borderWidth = 0.dp,
                                    radius = 8.dp,
                                    borderColor = Color.White,
                                    backgroundColor = Color.White
                                ),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageData.url)
                                .crossfade(100)
                                .build(),
                            contentDescription = null
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .roundedCornerShape(
                                    borderWidth = 0.dp,
                                    radius = 8.dp,
                                    borderColor = Color.White,
                                    backgroundColor = Color.White
                                )
                        ) {
                            ContentRow(
                                leftText = "id",
                                rightText = imageData.id
                            )
                            HorizontalDivider(thickness = 1.dp, color = NEUTRAL_GRAY_50)
                            ContentRow(
                                leftText = "author",
                                rightText = imageData.author
                            )
                            HorizontalDivider(thickness = 1.dp, color = NEUTRAL_GRAY_50)
                            ContentRow(
                                leftText = "Size",
                                rightText = imageData.size
                            )
                            HorizontalDivider(thickness = 1.dp, color = NEUTRAL_GRAY_50)
                            ContentRow(
                                leftText = "Created At",
                                rightText = imageData.createdAt
                            )
                        }
                        Spacer(Modifier.height(70.dp))
                    }

                    DetailMutate.State.ViewState.EMPTY -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 70.dp),
                                text = "이미지를 불러오는데 실패했습니다.",
                                style = CustomTheme.typography.title2,
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.End
            ) {
                CustomSnackBar(
                    message = snackBarState.message,
                    visible = snackBarState.visible,
                    onClick = { snackBarHandler.dismiss() }
                )
                Icon(
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                        .singleClickable(onClick = onClickBookmark)
                        .padding(16.dp),
                    imageVector = if (imageData.isBookmark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Red,
                )
            }
        }
    }
}

@Composable
private fun ContentRow(
    modifier: Modifier = Modifier,
    leftText: String,
    rightText: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier,
            text = leftText,
            style = CustomTheme.typography.body1,
        )
        Text(
            modifier = Modifier,
            text = rightText,
            style = CustomTheme.typography.body1.copy(color = GRAY),
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    DetailScreen(
        viewState = DetailMutate.State.ViewState.SHOW_DATA,
        imageData = DetailMutate.State.ImageData(
            id = "1",
            width = 1080,
            height = 1920,
            blurHash = "",
            url = "",
            author = "jooys",
            size = "1920 x 1080",
            createdAt = "2023-01-01",
            isBookmark = false
        ),
        clickBackButton = {},
        onClickBookmark = {},
        snackBarHandler = remember { SnackBarHandler() },
        snackBarState = SnackBarState(
            message = "테스트",
            visible = true
        )
    )
}
