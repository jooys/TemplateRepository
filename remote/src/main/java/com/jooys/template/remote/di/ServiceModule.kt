package com.jooys.template.remote.di

import com.jooys.template.remote.service.HomeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    private const val BASE_URL = "https://test.api.ddocdoc.kr"

    @Provides
    @Singleton
    fun provideHomeService(
        retrofitBuilder: Retrofit.Builder,
        @Named("provideCommonOkHttpClient") okHttpClient: OkHttpClient
    ): HomeService = retrofitBuilder
        .client(okHttpClient).baseUrl(BASE_URL).build()
        .create()

}
