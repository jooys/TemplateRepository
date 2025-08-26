package com.jooys.template.remote.service

import com.jooys.template.shared.model.home.IntelligenceEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET("v3/home/intelligence")
    suspend fun getIntelligence(
        @Query("needSN") needSN: Boolean = false
    ): IntelligenceEntity.Response
}
