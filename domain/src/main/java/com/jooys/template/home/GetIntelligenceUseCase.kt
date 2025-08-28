package com.jooys.template.home

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetIntelligenceUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(version: String) = flow {
        homeRepository.getIntelligence(version)
            .onSuccess {
                emit(Result.success(it))
            }
            .onFailure {
                emit(Result.failure(it))
            }
    }
}
