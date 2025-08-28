package com.jooys.template.home

import com.jooys.template.model.home.IntelligenceEntity

interface HomeRepository {

    suspend fun getIntelligence(needSN: Boolean = false): IntelligenceEntity.Response
}
