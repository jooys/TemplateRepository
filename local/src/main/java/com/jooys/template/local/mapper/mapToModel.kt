package com.jooys.template.local.mapper

import com.jooys.template.local.model.FavoriteItem
import com.jooys.template.model.photo.FavoriteItemLocalEntity


fun List<FavoriteItem>.mapToModel(): List<FavoriteItemLocalEntity> {
    return this.map { it.mapToModel() }
}

fun FavoriteItem.mapToModel(): FavoriteItemLocalEntity {
    return FavoriteItemLocalEntity(
        id = id,
        url = url,
    )
}
