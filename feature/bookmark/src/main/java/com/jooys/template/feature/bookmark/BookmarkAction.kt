package com.jooys.template.feature.bookmark

sealed interface BookmarkAction {
    data object OnViewCreated: BookmarkAction
    data object OnClickBackButton: BookmarkAction
    data object OnRefresh: BookmarkAction
    data class OnClickItem(val id: String): BookmarkAction
    data class OnItemClickBookmark(val item: BookmarkMutate.State.ImageItem): BookmarkAction
}
