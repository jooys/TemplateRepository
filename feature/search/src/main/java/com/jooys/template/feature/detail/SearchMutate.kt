package com.jooys.template.feature.detail

sealed interface SearchMutate {
    data class State(
        val viewState: ViewState = ViewState.INITIALIZE,
        val imageList: List<ImageItem> = emptyList(),
        val isLoading: Boolean = false,
        val page: Int = 1,
        val hasMore: Boolean = true,
        val searchText: String = "",
    ) {
        enum class ViewState {
            INITIALIZE,
            SHOW_DATA,
            EMPTY
        }

        data class ImageItem(
            val id: String,
            val url: String,
            val isBookmark: Boolean = false,
        )
    }

    sealed interface Reduce : SearchMutate {
        data class UpdateViewState(val viewState: State.ViewState) : Reduce
        data class UpdateImageList(val imageList: List<State.ImageItem>) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
        data class UpdatePage(val page: Int) : Reduce
        data class UpdateHasMore(val hasMore: Boolean) : Reduce
        data class UpdateSearchText(val searchText: String) : Reduce
    }

    sealed interface SideEffect : SearchMutate {
        data class ShowSnackBar(val message: String) : SideEffect
        data object Finish : SideEffect
        data class NaviToDetail(val id: String) : SideEffect
        data object NaviToBookmark : SideEffect
    }
}
