package com.jooys.template.remote.di

import com.jooys.template.remote.intercepor.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }
    @Provides
    @Singleton
    @Named("provideCommonOkHttpClient")
    fun provideCommonOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder(). apply {
            readTimeout(10, TimeUnit.SECONDS)
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
        }.build()
    }

    @Provides
    @Singleton
    fun provideBaseBuilder(
        kotlinXConverter: Converter.Factory
    ): Retrofit.Builder {
        return Retrofit.Builder().apply {
            addConverterFactory(kotlinXConverter)
        }
    }

    @Provides
    @Singleton
    fun providesKotlinxConverterFactory(): Converter.Factory {
        val json = Json { ignoreUnknownKeys = true }
        return json.asConverterFactory("application/json".toMediaType())
    }
}
