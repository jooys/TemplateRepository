package jooys.template.data.home

import com.jooys.template.home.HomeRepository
import com.jooys.template.model.home.IntelligenceEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource
): HomeRepository {


    override suspend fun getIntelligence(needSN: Boolean): IntelligenceEntity.Response {
        return homeRemoteDataSource.getIntelligence(needSN)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}
