package com.jooys.template.model.home

import kotlinx.serialization.Serializable

sealed class IntelligenceEntity {

    @Serializable
    data class Response(
        val platform: String,
        val version: String,
        val update: Boolean = false,
        val forceUpdate: Boolean = false,
        val description: String
    ) : IntelligenceEntity()
}
