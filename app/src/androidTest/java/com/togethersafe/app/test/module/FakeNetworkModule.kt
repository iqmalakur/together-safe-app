package com.togethersafe.app.test.module

import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.data.network.GeocodingService
import com.togethersafe.app.di.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object FakeNetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return object : ApiService {
            override suspend fun fetchIncidents(): List<Incident> {
                return emptyList()
            }
        }
    }

    @Provides
    @Singleton
    fun provideGeocodingService(): GeocodingService {
        return object : GeocodingService {
            override suspend fun searchLocation(query: String): List<GeocodingLocation> {
                return listOf(
                    GeocodingLocation(
                        lat = "123",
                        lon = "123",
                        name = "Cimahi",
                        display_name = "Cimahi",
                    )
                )
            }

        }
    }

}
