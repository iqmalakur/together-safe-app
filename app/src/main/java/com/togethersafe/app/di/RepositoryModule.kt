package com.togethersafe.app.di

import com.togethersafe.app.data.network.AuthService
import com.togethersafe.app.data.network.GeolocationService
import com.togethersafe.app.data.network.IncidentService
import com.togethersafe.app.data.network.ReportService
import com.togethersafe.app.repositories.AuthRepository
import com.togethersafe.app.repositories.GeolocationRepository
import com.togethersafe.app.repositories.IncidentRepository
import com.togethersafe.app.repositories.ReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(service: AuthService): AuthRepository {
        return AuthRepository(service)
    }

    @Provides
    @Singleton
    fun provideGeolocationRepository(service: GeolocationService): GeolocationRepository {
        return GeolocationRepository(service)
    }

    @Provides
    @Singleton
    fun provideIncidentRepository(service: IncidentService): IncidentRepository {
        return IncidentRepository(service)
    }

    @Provides
    @Singleton
    fun provideReportRepository(service: ReportService): ReportRepository {
        return ReportRepository(service)
    }

}
