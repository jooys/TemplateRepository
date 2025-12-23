package com.jooys.template.feature.detail

sealed interface DetailAction {
    data object OnViewCreated: DetailAction
    data object OnClickBack: DetailAction
    data class OnClickBookmark(val item: DetailMutate.State.ImageData): DetailAction
}
