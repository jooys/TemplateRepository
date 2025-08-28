package com.jooys.template.remote.home

import jooys.template.data.home.HomeRemoteDataSource
import com.jooys.template.model.home.IntelligenceEntity
import com.jooys.template.remote.service.HomeService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class HomeRemoteDataSourceImpl @Inject constructor(
    private val homeService: HomeService
) : HomeRemoteDataSource {

    override suspend fun getIntelligence(needSN: Boolean): IntelligenceEntity.Response {
        return homeService.getIntelligence(needSN)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindsHomeRemoteDataSource(
        homeRemoteDataSourceImpl: HomeRemoteDataSourceImpl
    ): HomeRemoteDataSource
}
