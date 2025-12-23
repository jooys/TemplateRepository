package com.jooys.template.domain.photo.repository

import com.jooys.template.model.photo.FavoriteItemLocalEntity


interface FavoriteItemLocalRepository {
    suspend fun getFavoriteItemList(): List<FavoriteItemLocalEntity>

    suspend fun saveFavoriteItem(id: String, url: String)

    suspend fun deleteFavoriteItem(id: String)

    suspend fun getFavoriteItem(id: String): FavoriteItemLocalEntity?
}
