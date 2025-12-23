package com.jooys.template.remote.home

import com.jooys.template.data.photo.PhotoRemoteDataSource
import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity
import com.jooys.template.remote.service.PhotoService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val photoService: PhotoService,
) : PhotoRemoteDataSource {
    override suspend fun getSearchPhoto(page: Int, query: String): ResultEntity<PhotoEntity.Response> {
        return photoService.getSearchPhoto(page, query)
    }

    override suspend fun getPhotoDetail(id: String): PhotoEntity.Response {
        return photoService.getPhotoDetail(id)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindsPhotoRemoteDataSource(
        photoRemoteDataSourceImpl: PhotoRemoteDataSourceImpl,
    ): PhotoRemoteDataSource
}
