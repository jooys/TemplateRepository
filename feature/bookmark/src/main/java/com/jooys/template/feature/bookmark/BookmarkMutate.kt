package com.jooys.template.feature.bookmark

sealed interface BookmarkMutate {
    data class State(
        val viewState: ViewState = ViewState.EMPTY,
        val imageList: List<ImageItem> = emptyList(),
        val isLoading: Boolean = false,
    ) {
        enum class ViewState {
            SHOW_DATA,
            EMPTY
        }

        data class ImageItem(
            val id: String,
            val url: String,
        )
    }

    sealed interface Reduce : BookmarkMutate {
        data class UpdateViewState(val viewState: State.ViewState) : Reduce
        data class UpdateImageList(val imageList: List<State.ImageItem>) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
    }

    sealed interface SideEffect : BookmarkMutate {
        data class ShowSnackBar(val message: String) : SideEffect
        data object Finish : SideEffect
        data class NaviToDetail(val id: String) : SideEffect
    }
}
