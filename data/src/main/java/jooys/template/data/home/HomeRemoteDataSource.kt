package jooys.template.data.home

import com.jooys.template.model.home.IntelligenceEntity

interface HomeRemoteDataSource {
    suspend fun getIntelligence(needSN: Boolean = false): IntelligenceEntity.Response
}
