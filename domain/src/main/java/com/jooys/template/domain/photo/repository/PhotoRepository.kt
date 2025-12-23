package com.jooys.template.domain.photo.repository

import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity

interface PhotoRepository {
    suspend fun getSearchPhoto(page: Int, query: String): ResultEntity<PhotoEntity.Response>
    suspend fun getPhotoDetail(id: String): PhotoEntity.Response
}
