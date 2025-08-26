package com.jooys.template.home

import com.jooys.template.shared.model.home.IntelligenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetIntelligenceUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(needSN: Boolean = false): Flow<Result<IntelligenceEntity.Response>> = flow {
        emit(runCatching { homeRepository.getIntelligence(needSN) })
    }
}
