package com.jooys.template.local.datasource

import com.jooys.template.data.local.FavoriteItemLocalDataSource
import com.jooys.template.local.FavoriteItemDao
import com.jooys.template.local.mapper.mapToModel
import com.jooys.template.local.model.FavoriteItem
import com.jooys.template.model.photo.FavoriteItemLocalEntity
import javax.inject.Inject

class FavoriteItemLocalDataSourceImpl @Inject constructor(
    private val favoriteItemDao: FavoriteItemDao,
) : FavoriteItemLocalDataSource {
    override suspend fun getFavoriteItemList(): List<FavoriteItemLocalEntity> {
        return favoriteItemDao.getFavoriteItemList().mapToModel()
    }

    override suspend fun saveFavoriteItem(id: String, url: String) {
        return favoriteItemDao.saveFavoriteItem(FavoriteItem(id, url))
    }

    override suspend fun deleteFavoriteItem(id: String) {
        return favoriteItemDao.deleteFavoriteItem(id)
    }

    override suspend fun getFavoriteItem(id: String): FavoriteItemLocalEntity? {
        return favoriteItemDao.getFavoriteItem(id)?.mapToModel()
    }
}
