package com.togethersafe.app.test.module

import com.togethersafe.app.constants.MapConstants.LATITUDE_DEFAULT
import com.togethersafe.app.constants.MapConstants.LONGITUDE_DEFAULT
import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.data.model.IncidentReport
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

    const val LATITUDE = LATITUDE_DEFAULT + 5
    const val LONGITUDE = LONGITUDE_DEFAULT + 5

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return object : ApiService {
            override suspend fun fetchIncidents(): List<Incident> {
                return listOf(
                    Incident(
                        category = "Testing",
                        date = "01 Januari 1970",
                        time = "00:00",
                        riskLevel = "high",
                        location = "Jl. Testing",
                        latitude = LATITUDE_DEFAULT,
                        longitude = LONGITUDE_DEFAULT,
                        status = "active",
                        mediaUrls = listOf("https://picsum.photos/200"),
                        reports = listOf(
                            IncidentReport(
                                id = "abc",
                                description = "lorem ipsum dolor sit amet",
                            ),
                        ),
                    )
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideGeocodingService(): GeocodingService {
        return object : GeocodingService {
            override suspend fun searchLocation(query: String): List<GeocodingLocation> {
                return when (query) {
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
