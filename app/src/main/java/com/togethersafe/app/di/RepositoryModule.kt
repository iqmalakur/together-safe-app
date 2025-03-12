package com.togethersafe.app.di

import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.repository.IncidentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideIncidentRepository(apiService: ApiService): IncidentRepository {
        return IncidentRepository(apiService)
    }

}
