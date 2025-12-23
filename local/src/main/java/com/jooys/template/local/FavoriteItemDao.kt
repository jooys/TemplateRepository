package com.jooys.template.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jooys.template.local.model.FavoriteItem

@Dao
interface FavoriteItemDao {

    @Query("SELECT * FROM FavoriteItem")
    suspend fun getFavoriteItemList(): List<FavoriteItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavoriteItem(item: FavoriteItem)

    @Query("DELETE FROM FavoriteItem WHERE id = :id")
    suspend fun deleteFavoriteItem(id: String)

    @Query("SELECT * FROM FavoriteItem WHERE id = :id")
    suspend fun getFavoriteItem(id: String): FavoriteItem?
}
