package com.togethersafe.app.di

import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.data.network.AuthService
import com.togethersafe.app.data.network.GeocodingService
import com.togethersafe.app.data.network.IncidentService
import com.togethersafe.app.data.network.ReportService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://stirred-eagle-witty.ngrok-free.app")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideIncidentService(retrofit: Retrofit): IncidentService =
        retrofit.create(IncidentService::class.java)

    @Provides
    @Singleton
    fun provideReportService(retrofit: Retrofit): ReportService =
        retrofit.create(ReportService::class.java)

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://stirred-eagle-witty.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingService(): GeocodingService {
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

}
