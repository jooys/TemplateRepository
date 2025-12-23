package com.jooys.template.data.photo

import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity

interface PhotoRemoteDataSource {

    suspend fun getSearchPhoto(page: Int, query: String): ResultEntity<PhotoEntity.Response>
    suspend fun getPhotoDetail(id: String): PhotoEntity.Response
}
