package com.jooys.template.remote.service

import com.jooys.template.model.home.IntelligenceEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET("v2/version")
    suspend fun getIntelligence(
        @Query("version") version: String,
        @Query("platform") platform: String
    ): IntelligenceEntity.Response
}
