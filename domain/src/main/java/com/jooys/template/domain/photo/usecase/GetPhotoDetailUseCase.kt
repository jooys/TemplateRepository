package com.jooys.template.domain.photo.usecase

import com.jooys.template.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
) {
    operator fun invoke(id: String) = flow {
        val result = photoRepository.getPhotoDetail(id)
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(it))
    }
}
