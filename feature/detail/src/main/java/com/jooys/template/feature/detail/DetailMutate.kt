package com.jooys.template.feature.detail

sealed interface DetailMutate {
    data class State(
        val isLoading: Boolean = false,
        val viewState: ViewState = ViewState.INITIALIZE,
        val imageData: ImageData = ImageData(),
    ) {
        enum class ViewState {
            INITIALIZE,
            SHOW_DATA,
            EMPTY
        }

        data class ImageData(
            val id: String,
            val width: Int,
            val height: Int,
            val blurHash: String,
            val url: String,
            val author: String,
            val size: String,
            val createdAt: String,
            val isBookmark: Boolean,
        ) {
            constructor() : this(
                id = "",
                width = 0,
                height = 0,
                blurHash = "",
                url = "",
                author = "",
                size = "",
                createdAt = "",
                isBookmark = false
            )
        }
    }

    sealed interface Reduce : DetailMutate {
        data class UpdateViewState(val viewState: State.ViewState) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
        data class UpdateImageData(val imageData: State.ImageData) : Reduce
        data class UpdateIsBookmark(val isBookmark: Boolean) : Reduce
    }

    sealed interface SideEffect : DetailMutate {
        data class ShowSnackBar(val message: String) : SideEffect
        data object Finish : SideEffect
    }
}
