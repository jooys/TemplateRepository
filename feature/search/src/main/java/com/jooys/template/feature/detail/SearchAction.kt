package com.jooys.template.feature.detail

sealed interface SearchAction {
    data object OnViewCreated : SearchAction
    data object OnClickBookmark : SearchAction
    data object OnLoadMore : SearchAction
    data object OnRefreshBookmark : SearchAction
    data class OnTextChanged(val text: String) : SearchAction
    data class OnSearch(val text: String) : SearchAction
    data class OnClickItem(val id: String) : SearchAction
    data class OnItemClickBookmark(val item: SearchMutate.State.ImageItem) : SearchAction
}
