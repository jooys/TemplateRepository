package com.jooys.template.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jooys.template.local.model.FavoriteItem

@Database(
    entities = [
        FavoriteItem::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class BaseDatabase : RoomDatabase() {

    abstract fun favoriteItemDao(): FavoriteItemDao

}
