package com.jooys.template.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.jooys.template.domain.photo.usecase.GetFavoriteItemDetailUseCase
import com.jooys.template.domain.photo.usecase.GetPhotoDetailUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DetailMutateHandler @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val getFavoriteItemDetailUseCase: GetFavoriteItemDetailUseCase,
    private val saveAndDeleteFavoriteItemUseCase: SaveAndDeleteFavoriteItemUseCase,
) {

    fun mutate(action: DetailAction) = flow {
        when (action) {
            DetailAction.OnViewCreated -> {
                val id = savedStateHandle.get<String>("id").toString()
                getPhotoDetailUseCase.invoke(id)
                    .combine(getFavoriteItemDetailUseCase.invoke(id)) { imageResult, favoriteResult ->
                        (imageResult to favoriteResult)
                    }
                    .onStart { emit(DetailMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(DetailMutate.Reduce.UpdateLoading(false)) }
                    .collect {
                        it.first.onSuccess { imageResult ->
                            it.second.onSuccess { favoriteResult ->
                                emit(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.SHOW_DATA))
                                emit(
                                    DetailMutate.Reduce.UpdateImageData(
                                        DetailMutate.State.ImageData(
                                            id = imageResult.id,
                                            width = imageResult.width,
                                            height = imageResult.height,
                                            blurHash = imageResult.blurHash,
                                            url = imageResult.urls.raw,
                                            author = imageResult.user.username,
                                            size = "${imageResult.width} x ${imageResult.height}",
                                            createdAt = if (imageResult.createdAt.isNotEmpty()) getDateConvertString(imageResult.createdAt, "yyyy년 M월 d일 (E) a h:mm") else "",
                                            isBookmark = favoriteResult != null
                                        )
                                    )
                                )
                            }.onFailure {
                                it.printStackTrace()
                                emit(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.EMPTY))
                                emit(DetailMutate.SideEffect.ShowSnackBar(it.message.toString()))
                            }
                        }.onFailure {
                            it.printStackTrace()
                            emit(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.EMPTY))
                            emit(DetailMutate.SideEffect.ShowSnackBar(it.message.toString()))
                        }
                    }
            }

            DetailAction.OnClickBack -> {
                emit(DetailMutate.SideEffect.Finish)
            }

            is DetailAction.OnClickBookmark -> {
                saveAndDeleteFavoriteItemUseCase.invoke(action.item.isBookmark, action.item.id, action.item.url)
                    .onStart { emit(DetailMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(DetailMutate.Reduce.UpdateLoading(false)) }
                    .collect {
                        it.onSuccess {
                            if (action.item.isBookmark) {
                                emit(DetailMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                            } else {
                                emit(DetailMutate.SideEffect.ShowSnackBar("북마크에 저장되었습니다."))
                            }
                            emit(DetailMutate.Reduce.UpdateIsBookmark(!action.item.isBookmark))
                        }.onFailure {
                            it.printStackTrace()
                            emit(DetailMutate.SideEffect.ShowSnackBar(it.message.toString()))
                        }
                    }
            }
        }
    }

    fun getDateConvertString(input: String, pattern: String): String {
        return Instant.parse(input)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(pattern))
    }
}
