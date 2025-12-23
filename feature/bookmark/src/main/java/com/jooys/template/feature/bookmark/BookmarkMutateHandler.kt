package com.jooys.template.feature.bookmark

import com.jooys.template.domain.photo.usecase.GetFavoriteItemListUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import com.jooys.template.model.photo.FavoriteItemLocalEntity
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class BookmarkMutateHandler @Inject constructor(
    private val getFavoriteItemListUseCase: GetFavoriteItemListUseCase,
    private val saveAndDeleteFavoriteItemUseCase: SaveAndDeleteFavoriteItemUseCase,
) {

    fun mutate(action: BookmarkAction) = flow {
        when (action) {
            BookmarkAction.OnViewCreated,
            BookmarkAction.OnRefresh,
                -> {
                getFavoriteItemListUseCase.invoke()
                    .onStart { emit(BookmarkMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(BookmarkMutate.Reduce.UpdateLoading(false)) }
                    .collect {
                        it.onSuccess { result ->
                            responseBookmarkList(result)
                        }.onFailure { error ->
                            error.printStackTrace()
                            emit(BookmarkMutate.Reduce.UpdateViewState(BookmarkMutate.State.ViewState.EMPTY))
                            emit(BookmarkMutate.SideEffect.ShowSnackBar(error.message.toString()))
                        }
                    }
            }

            BookmarkAction.OnClickBackButton -> {
                emit(BookmarkMutate.SideEffect.Finish)
            }

            is BookmarkAction.OnClickItem -> {
                emit(BookmarkMutate.SideEffect.NaviToDetail(action.id))
            }

            is BookmarkAction.OnItemClickBookmark -> {
                saveAndDeleteFavoriteItemUseCase.invoke(true, action.item.id, action.item.url)
                    .onStart { emit(BookmarkMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(BookmarkMutate.Reduce.UpdateLoading(false)) }
                    .flatMapConcat { getFavoriteItemListUseCase.invoke() }
                    .collect {
                        it.onSuccess { result ->
                            emit(BookmarkMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                            responseBookmarkList(result)
                        }.onFailure { error ->
                            error.printStackTrace()
                            emit(BookmarkMutate.Reduce.UpdateViewState(BookmarkMutate.State.ViewState.EMPTY))
                            emit(BookmarkMutate.SideEffect.ShowSnackBar(error.message.toString()))
                        }
                    }
            }
        }
    }

    private suspend fun FlowCollector<BookmarkMutate>.responseBookmarkList(result: List<FavoriteItemLocalEntity>) {
        if (result.isEmpty()) {
            emit(BookmarkMutate.Reduce.UpdateViewState(BookmarkMutate.State.ViewState.EMPTY))
        } else {
            emit(BookmarkMutate.Reduce.UpdateViewState(BookmarkMutate.State.ViewState.SHOW_DATA))
            emit(BookmarkMutate.Reduce.UpdateImageList(result.map {
                BookmarkMutate.State.ImageItem(
                    id = it.id,
                    url = it.url,
                )
            }))
        }
    }
}
