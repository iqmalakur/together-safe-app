package com.togethersafe.app.di

import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.repositories.IncidentRepository
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
    fun provideIncidentRepository(apiService: ApiService): IncidentRepository {
        return IncidentRepository(apiService)
    }

}
