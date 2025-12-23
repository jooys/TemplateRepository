package com.jooys.template.domain.photo.usecase

import com.jooys.template.domain.photo.repository.FavoriteItemLocalRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveAndDeleteFavoriteItemUseCase @Inject constructor(
    private val favoriteItemLocalRepository: FavoriteItemLocalRepository,
) {
    operator fun invoke(isSavedFavorite: Boolean, id: String, url: String) = flow {
        val response = if (isSavedFavorite) {
            favoriteItemLocalRepository.deleteFavoriteItem(id)
        } else {
            favoriteItemLocalRepository.saveFavoriteItem(id, url)
        }
        emit(Result.success(response))
    }.catch {
        emit(Result.failure(it))
    }
}
