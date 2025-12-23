package com.jooys.template.model.photo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class PhotoEntity {

    @Serializable
    data class Response(
        val id: String,
        @SerialName("created_at") val createdAt: String,
        val width: Int,
        val height: Int,
        val urls: Urls,
        val bookmarked: Boolean,
        val user: UnsplashUser,
        @SerialName("blur_hash") val blurHash: String,
    )

    @Serializable
    data class Urls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String,
    )


    @Serializable
    data class UnsplashUser(
        val id: String,
        val username: String,
    )
}
