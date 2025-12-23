package com.jooys.template.data.photo

import com.jooys.template.domain.photo.repository.PhotoRepository
import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class PhotoRepositoryImpl @Inject constructor(
    private val photoRemoteDataSource: PhotoRemoteDataSource,
) : PhotoRepository {
    override suspend fun getSearchPhoto(page: Int, query: String): ResultEntity<PhotoEntity.Response> {
        return photoRemoteDataSource.getSearchPhoto(page, query)
    }

    override suspend fun getPhotoDetail(id: String): PhotoEntity.Response {
        return photoRemoteDataSource.getPhotoDetail(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl,
    ): PhotoRepository
}
