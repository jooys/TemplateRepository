package com.jooys.template.feature.bookmark

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.jooys.template.core.component.compose.CustomSnackBar
import com.jooys.template.core.component.compose.SnackBarHandler
import com.jooys.template.core.component.compose.SnackBarState
import com.jooys.template.core.component.compose.ToolbarWithBackButton
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.core.util.findActivity
import com.jooys.template.core.util.singleClickable
import com.jooys.template.feature.detail.navigation.DetailNavigation
import androidx.lifecycle.viewmodel.compose.viewModel as ComposableViewModel

@Composable
fun BookmarkRoute(
    viewModel: BookmarkViewModel = ComposableViewModel(),
    detailNavigation: DetailNavigation,
) {
    val activity = LocalContext.current.findActivity()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHandler = remember { SnackBarHandler() }
    val snackBarState by snackBarHandler.snackBarState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.onAction(BookmarkAction.OnRefresh)
    }

    BookmarkScreen(
        viewState = state.viewState,
        imageList = state.imageList,
        clickBackButton = {
            viewModel.onAction(BookmarkAction.OnClickBackButton)
        },
        onClickItem = {
            viewModel.onAction(BookmarkAction.OnClickItem(it))
        },
        onItemClickBookmark = {
            viewModel.onAction(BookmarkAction.OnItemClickBookmark(it))
        },
        snackBarHandler = snackBarHandler,
        snackBarState = snackBarState
    )

    LaunchedEffect(Unit) {
        viewModel.onAction(BookmarkAction.OnViewCreated)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                BookmarkMutate.SideEffect.Finish -> {
                    activity.finish()
                }

                is BookmarkMutate.SideEffect.ShowSnackBar -> {
                    snackBarHandler.show(sideEffect.message)
                }

                is BookmarkMutate.SideEffect.NaviToDetail -> {
                    launcher.launch(detailNavigation.getDetailIntent(activity, sideEffect.id))
                }
            }
        }
    }
}

@Composable
internal fun BookmarkScreen(
    viewState: BookmarkMutate.State.ViewState,
    imageList: List<BookmarkMutate.State.ImageItem>,
    clickBackButton: () -> Unit,
    onClickItem: (String) -> Unit,
    onItemClickBookmark: (BookmarkMutate.State.ImageItem) -> Unit,
    snackBarHandler: SnackBarHandler,
    snackBarState: SnackBarState,
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            ToolbarWithBackButton("북마크 리스트", clickBackButton = clickBackButton)
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            when (viewState) {
                BookmarkMutate.State.ViewState.SHOW_DATA -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(
                                items = imageList,
                                key = { _, item -> item.id }
                            ) { index, item ->
                                ImageItem(
                                    item = item,
                                    onItemClick = onClickItem,
                                    onItemClickBookmark = onItemClickBookmark
                                )
                            }
                        }
                    }
                }

                BookmarkMutate.State.ViewState.EMPTY -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp),
                        text = "저장된 북마크가 없습니다.",
                        style = CustomTheme.typography.title1,
                        textAlign = TextAlign.Center
                    )
                }
            }
            CustomSnackBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                message = snackBarState.message,
                visible = snackBarState.visible,
                onClick = { snackBarHandler.dismiss() }
            )
        }
    }
}

@Composable
private fun ImageItem(
    item: BookmarkMutate.State.ImageItem,
    onItemClick: (String) -> Unit,
    onItemClickBookmark: (BookmarkMutate.State.ImageItem) -> Unit,
) {
    Box(
        modifier = Modifier
            .singleClickable(onClick = {
                onItemClick.invoke(item.id)
            }),
        contentAlignment = Alignment.TopEnd
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.url)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .singleClickable(onClick = { onItemClickBookmark.invoke(item) })
                .size(24.dp),
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Red
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    BookmarkScreen(
        viewState = BookmarkMutate.State.ViewState.SHOW_DATA,
        imageList = listOf(
            BookmarkMutate.State.ImageItem(
                id = "1",
                url = ""
            )
        ),
        clickBackButton = {},
        onClickItem = {},
        onItemClickBookmark = {},
        snackBarHandler = remember { SnackBarHandler() },
        snackBarState = SnackBarState(
            message = "테스트",
            visible = true
        )
    )
}
