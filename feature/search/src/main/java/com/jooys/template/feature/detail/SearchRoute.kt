@file:OptIn(ExperimentalMaterial3Api::class)

package com.jooys.template.feature.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.jooys.template.core.component.compose.CustomLoading
import com.jooys.template.core.component.compose.CustomSnackBar
import com.jooys.template.core.component.compose.SnackBarHandler
import com.jooys.template.core.component.compose.SnackBarState
import com.jooys.template.core.component.compose.ToolbarWithBackButton
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.core.theme.GRAY
import com.jooys.template.core.theme.NEUTRAL_GRAY_50
import com.jooys.template.core.util.OnBottomReached
import com.jooys.template.core.util.findActivity
import com.jooys.template.core.util.roundedCornerShape
import com.jooys.template.core.util.singleClickable
import com.jooys.template.feature.bookmark.navigation.BookmarkNavigation
import com.jooys.template.feature.detail.navigation.DetailNavigation
import androidx.lifecycle.viewmodel.compose.viewModel as ComposableViewModel

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = ComposableViewModel(),
    detailNavigation: DetailNavigation,
    bookmarkNavigation: BookmarkNavigation,
) {
    val activity = LocalContext.current.findActivity()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHandler = remember { SnackBarHandler() }
    val snackBarState by snackBarHandler.snackBarState.collectAsStateWithLifecycle()

    val listState: LazyGridState = rememberLazyGridState()
    val focusManager = LocalFocusManager.current

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.onAction(SearchAction.OnRefreshBookmark)
    }

    SearchScreen(
        viewState = state.viewState,
        searchText = state.searchText,
        imageList = state.imageList,
        listState = listState,
        onTextChanged = {
            viewModel.onAction(SearchAction.OnTextChanged(it))
        },
        onClickBookmark = {
            viewModel.onAction(SearchAction.OnClickBookmark)
        },
        onSearch = {
            focusManager.clearFocus()
            viewModel.onAction(SearchAction.OnSearch(it))
        },
        loadMore = {
            viewModel.onAction(SearchAction.OnLoadMore)
        },
        onClickItem = {
            viewModel.onAction(SearchAction.OnClickItem(it))
        },
        onItemClickBookmark = {
            viewModel.onAction(SearchAction.OnItemClickBookmark(it))
        },
        snackBarHandler = snackBarHandler,
        snackBarState = snackBarState
    )

    if (state.isLoading) {
        CustomLoading()
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(SearchAction.OnViewCreated)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                SearchMutate.SideEffect.Finish -> {
                    activity.finishAffinity()
                }

                is SearchMutate.SideEffect.ShowSnackBar -> {
                    snackBarHandler.show(sideEffect.message)
                }

                is SearchMutate.SideEffect.NaviToDetail -> {
                    launcher.launch(detailNavigation.getDetailIntent(activity, sideEffect.id))
                }

                SearchMutate.SideEffect.NaviToBookmark -> {
                    launcher.launch(bookmarkNavigation.getBookmarkIntent(activity))
                }
            }
        }
    }
}

@Composable
internal fun SearchScreen(
    viewState: SearchMutate.State.ViewState,
    searchText: String,
    imageList: List<SearchMutate.State.ImageItem>,
    listState: LazyGridState,
    onClickBookmark: () -> Unit,
    loadMore: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClickItem: (String) -> Unit,
    onItemClickBookmark: (SearchMutate.State.ImageItem) -> Unit,
    snackBarHandler: SnackBarHandler,
    snackBarState: SnackBarState,
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            SearchSection(
                searchText = searchText,
                onClickBookmark = onClickBookmark,
                onTextChanged = onTextChanged,
                onSearch = onSearch
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            when(viewState) {
                SearchMutate.State.ViewState.INITIALIZE -> {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp),
                        text = "검색어를 입력하면\n이미지가 검색됩니다.",
                        style = CustomTheme.typography.title1,
                        textAlign = TextAlign.Center
                    )
                }
                SearchMutate.State.ViewState.SHOW_DATA -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            state = listState,
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
                        listState.OnBottomReached(loadMore = loadMore)
                    }

                }
                SearchMutate.State.ViewState.EMPTY -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp),
                        text = "검색결과가 없습니다.\n다른 검색어를 입력해주세요.",
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
private fun SearchSection(
    searchText: String,
    onClickBookmark: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp),
                text = "Search",
                style = CustomTheme.typography.title2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .singleClickable(onClick = onClickBookmark)
                    .padding(14.dp),
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red
            )
        }
        BasicTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .roundedCornerShape(
                    borderWidth = 1.dp,
                    borderColor = NEUTRAL_GRAY_50,
                    backgroundColor = NEUTRAL_GRAY_50,
                    radius = 8.dp
                ),
            textStyle = CustomTheme.typography.body1,
            decorationBox = {
                TextFieldDefaults.DecorationBox(
                    value = searchText,
                    innerTextField = it,
                    singleLine = true,
                    interactionSource = remember { MutableInteractionSource() },
                    placeholder = {
                        Text(
                            maxLines = 1,
                            text = "검색어를 입력하세요",
                            style = CustomTheme.typography.body1,
                            color = GRAY,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        start = 16.dp,
                        top = 14.dp,
                        end = 16.dp,
                        bottom = 14.dp,
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledTextColor = GRAY,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    enabled = true,
                    visualTransformation = VisualTransformation.None
                )
            },
            value = searchText,
            onValueChange = onTextChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions {
                onSearch.invoke(searchText)
            }
        )
    }
}


@Composable
private fun ImageItem(
    item: SearchMutate.State.ImageItem,
    onItemClick: (String) -> Unit,
    onItemClickBookmark: (SearchMutate.State.ImageItem) -> Unit,
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
            imageVector = if (item.isBookmark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = if (item.isBookmark) Color.Red else Color.White
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewGalleryListContent() {
    SearchScreen(
        viewState = SearchMutate.State.ViewState.SHOW_DATA,
        searchText = "",
        imageList = listOf(
            SearchMutate.State.ImageItem(
                id = "1",
                url = ""
            )
        ),
        listState = rememberLazyGridState(),
        onClickBookmark = {},
        onTextChanged = {},
        onSearch = {},
        loadMore = {},
        onClickItem = {},
        onItemClickBookmark = {},
        snackBarHandler = remember { SnackBarHandler() },
        snackBarState = SnackBarState(
            message = "테스트",
            visible = true
        )
    )
}
