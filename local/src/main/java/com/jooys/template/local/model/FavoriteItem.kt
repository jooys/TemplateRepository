package com.jooys.template.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favoriteItem")
data class FavoriteItem(
    @PrimaryKey val id: String,
    val url: String,
)
