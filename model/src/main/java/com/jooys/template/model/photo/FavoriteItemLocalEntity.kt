package com.jooys.template.model.photo
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteItemLocalEntity(
    val id: String,
    val url: String,
)
