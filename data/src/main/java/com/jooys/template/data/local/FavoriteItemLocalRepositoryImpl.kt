package com.jooys.template.data.local

import com.jooys.template.domain.photo.repository.FavoriteItemLocalRepository
import com.jooys.template.model.photo.FavoriteItemLocalEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class FavoriteItemLocalRepositoryImpl @Inject constructor(
    private val favoriteItemLocalDataSource: FavoriteItemLocalDataSource,
) : FavoriteItemLocalRepository {
    override suspend fun getFavoriteItemList(): List<FavoriteItemLocalEntity> {
        return favoriteItemLocalDataSource.getFavoriteItemList()
    }

    override suspend fun saveFavoriteItem(id: String, url: String) {
        return favoriteItemLocalDataSource.saveFavoriteItem(id, url)
    }

    override suspend fun deleteFavoriteItem(id: String) {
        return favoriteItemLocalDataSource.deleteFavoriteItem(id)
    }

    override suspend fun getFavoriteItem(id: String): FavoriteItemLocalEntity? {
        return favoriteItemLocalDataSource.getFavoriteItem(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteItemLocalRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsFavoriteItemLocalRepository(
        favoriteItemLocalRepositoryImpl: FavoriteItemLocalRepositoryImpl,
    ): FavoriteItemLocalRepository
}
