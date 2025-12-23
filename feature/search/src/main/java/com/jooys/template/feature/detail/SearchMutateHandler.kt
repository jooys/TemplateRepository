package com.jooys.template.feature.detail

import com.jooys.template.domain.photo.usecase.GetFavoriteItemListUseCase
import com.jooys.template.domain.photo.usecase.GetSearchPhotoUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SearchMutateHandler @Inject constructor(
    private val getSearchPhotoUseCase: GetSearchPhotoUseCase,
    private val getFavoriteItemListUseCase: GetFavoriteItemListUseCase,
    private val saveAndDeleteFavoriteItemUseCase: SaveAndDeleteFavoriteItemUseCase,
) {

    fun mutate(action: SearchAction, state: SearchMutate.State) = flow {
        when (action) {
            SearchAction.OnViewCreated -> {
                emit(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.INITIALIZE))
            }

            SearchAction.OnRefreshBookmark -> {
                getFavoriteItemListUseCase.invoke()
                    .collect {
                        it.onSuccess { result ->
                            val favoriteMap = result.associateBy { it.id }
                            val resultList = state.imageList.map { item ->
                                item.copy(
                                    isBookmark = favoriteMap.containsKey(item.id)
                                )
                            }
                            emit(SearchMutate.Reduce.UpdateImageList(resultList))

                        }.onFailure { error ->
                            error.printStackTrace()
                            emit(SearchMutate.SideEffect.ShowSnackBar(error.message.toString()))
                        }
                    }
            }

            is SearchAction.OnTextChanged -> {
                emit(SearchMutate.Reduce.UpdateSearchText(action.text))
            }

            is SearchAction.OnSearch -> {
                requestSearchImage(
                    query = action.text,
                    page = 1,
                    list = emptyList()
                )
            }

            SearchAction.OnLoadMore -> {
                if (state.hasMore) {
                    requestSearchImage(
                        query = state.searchText,
                        page = state.page,
                        list = state.imageList
                    )
                }
            }

            is SearchAction.OnClickItem -> {
                emit(SearchMutate.SideEffect.NaviToDetail(action.id))
            }

            is SearchAction.OnItemClickBookmark -> {
                requestBookMark(action.item, state)
            }

            SearchAction.OnClickBookmark -> {
                emit(SearchMutate.SideEffect.NaviToBookmark)
            }
        }
    }

    private suspend fun FlowCollector<SearchMutate>.requestBookMark(item: SearchMutate.State.ImageItem, state: SearchMutate.State) {
        saveAndDeleteFavoriteItemUseCase.invoke(item.isBookmark, item.id, item.url)
            .onStart { emit(SearchMutate.Reduce.UpdateLoading(true)) }
            .onCompletion { emit(SearchMutate.Reduce.UpdateLoading(false)) }
            .collect {
                it.onSuccess {
                    if (item.isBookmark) {
                        emit(SearchMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                    } else {
                        emit(SearchMutate.SideEffect.ShowSnackBar("북마크에 저장되었습니다."))
                    }
                    emit(SearchMutate.Reduce.UpdateImageList(state.imageList.map { it ->
                        if (it.id == item.id) {
                            it.copy(isBookmark = !item.isBookmark)
                        } else it
                    }))
                }.onFailure { error ->
                    error.printStackTrace()
                    emit(SearchMutate.SideEffect.ShowSnackBar(error.message.toString()))
                }
            }
    }

    private suspend fun FlowCollector<SearchMutate>.requestSearchImage(query: String, page: Int, list: List<SearchMutate.State.ImageItem>) {
        getSearchPhotoUseCase.invoke(query = query, page = page)
            .combine(getFavoriteItemListUseCase.invoke()) { imageResult, favoriteResult ->
                (imageResult to favoriteResult)
            }
            .onStart { emit(SearchMutate.Reduce.UpdateLoading(true)) }
            .onCompletion { emit(SearchMutate.Reduce.UpdateLoading(false)) }
            .collect {
                it.first.onSuccess { result ->
                    it.second.onSuccess { favoriteList ->
                        val favoriteMap = favoriteList.associateBy { it.id }
                        val resultList = list + result.results.map { item ->
                            SearchMutate.State.ImageItem(
                                id = item.id,
                                url = item.urls.thumb,
                                isBookmark = favoriteMap.containsKey(item.id)
                            )
                        }
                        if (resultList.isEmpty()) {
                            emit(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.EMPTY))
                        } else {
                            emit(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.SHOW_DATA))
                            emit(SearchMutate.Reduce.UpdateImageList(resultList))
                            emit(SearchMutate.Reduce.UpdatePage(page + 1))
                            val hasMore = page + 1 < result.totalPages
                            emit(SearchMutate.Reduce.UpdateHasMore(hasMore))
                        }
                    }.onFailure { error ->
                        error.printStackTrace()
                        emit(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.EMPTY))
                        emit(SearchMutate.SideEffect.ShowSnackBar(error.message.toString()))
                    }
                }.onFailure { error ->
                    error.printStackTrace()
                    emit(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.EMPTY))
                    emit(SearchMutate.SideEffect.ShowSnackBar(error.message.toString()))
                }
            }
    }
}
