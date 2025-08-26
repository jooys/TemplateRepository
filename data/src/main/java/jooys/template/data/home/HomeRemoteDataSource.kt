package jooys.template.data.home

import com.jooys.template.shared.model.home.IntelligenceEntity

interface HomeRemoteDataSource {
    suspend fun getIntelligence(needSN: Boolean = false): IntelligenceEntity.Response
}
