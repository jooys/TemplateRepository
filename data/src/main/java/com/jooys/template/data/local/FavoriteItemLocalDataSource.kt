package com.jooys.template.data.local

import com.jooys.template.model.photo.FavoriteItemLocalEntity

interface FavoriteItemLocalDataSource {
    suspend fun getFavoriteItemList(): List<FavoriteItemLocalEntity>

    suspend fun saveFavoriteItem(id: String, url: String)

    suspend fun deleteFavoriteItem(id: String)

    suspend fun getFavoriteItem(id: String): FavoriteItemLocalEntity?
}
