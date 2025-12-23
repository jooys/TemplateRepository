package com.jooys.template.domain.photo.usecase

import com.jooys.template.domain.photo.repository.FavoriteItemLocalRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFavoriteItemDetailUseCase @Inject constructor(
    private val favoriteItemLocalRepository: FavoriteItemLocalRepository,
) {
    operator fun invoke(id: String) = flow {
        val detail = favoriteItemLocalRepository.getFavoriteItem(id)
        emit(Result.success(detail))
    }.catch {
        emit(Result.failure(it))
    }
}
