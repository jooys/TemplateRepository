package com.jooys.template.domain.photo.usecase

import com.jooys.template.domain.photo.repository.FavoriteItemLocalRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFavoriteItemListUseCase @Inject constructor(
    private val favoriteItemLocalRepository: FavoriteItemLocalRepository,
) {
    operator fun invoke() = flow {
        val response = favoriteItemLocalRepository.getFavoriteItemList()
        emit(Result.success(response))
    }.catch {
        emit(Result.failure(it))
    }
}

