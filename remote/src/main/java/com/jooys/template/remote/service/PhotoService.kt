package com.jooys.template.remote.service

import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoService {

    @GET("search/photos")
    suspend fun getSearchPhoto(
        @Query("page") page: Int,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20,
    ): ResultEntity<PhotoEntity.Response>

    @GET("/photos/{id}")
    suspend fun getPhotoDetail(
        @Path("id") id: String,
    ): PhotoEntity.Response
}
