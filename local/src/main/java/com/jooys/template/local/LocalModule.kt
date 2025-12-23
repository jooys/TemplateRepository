package com.jooys.template.local

import android.content.Context
import androidx.room.Room
import com.jooys.template.data.local.FavoriteItemLocalDataSource
import com.jooys.template.local.datasource.FavoriteItemLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesBaseDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        BaseDatabase::class.java,
        "base-database"
    ).build()

    @Provides
    @Singleton
    fun provideFavoriteItemDaoDao(
        database: BaseDatabase,
    ): FavoriteItemDao {
        return database.favoriteItemDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteItemLocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun favoriteItemLocalDataSource(
        favoriteItemLocalDataSourceImpl: FavoriteItemLocalDataSourceImpl,
    ): FavoriteItemLocalDataSource
}
