package com.jooys.template.domain.photo.usecase

import com.jooys.template.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSearchPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
) {
    operator fun invoke(query: String, page: Int) = flow {
        val result = photoRepository.getSearchPhoto(page, query)
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(it))
    }
}
