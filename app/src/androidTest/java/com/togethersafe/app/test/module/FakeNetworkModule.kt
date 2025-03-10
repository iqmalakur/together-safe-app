package com.togethersafe.app.test.module

import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.data.network.GeocodingService
import com.togethersafe.app.di.NetworkModule
import com.togethersafe.app.utils.MapConfig.LATITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.LONGITUDE_DEFAULT
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

    const val LATITUDE = LATITUDE_DEFAULT + 5
    const val LONGITUDE = LONGITUDE_DEFAULT + 5

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
                return when(query) {
                    "Testing" -> listOf(
                        GeocodingLocation(
                            lat = "$LATITUDE",
                            lon = "$LONGITUDE",
                            name = "Testing",
                            display_name = "Testing",
                        )
                    )
                    else -> emptyList()
                }
            }
        }
    }

}
