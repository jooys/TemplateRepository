package jooys.template.data.home

import com.jooys.template.model.home.IntelligenceEntity

interface HomeRemoteDataSource {
    suspend fun getIntelligence(version: String): IntelligenceEntity.Response
}
