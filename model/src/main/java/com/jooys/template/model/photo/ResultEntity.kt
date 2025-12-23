package com.jooys.template.model.photo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultEntity<T>(
    val total: Int,
    @SerialName("total_pages") val totalPages: Int,
    val results: List<T>,
)
