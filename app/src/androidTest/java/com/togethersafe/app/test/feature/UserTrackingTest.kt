package com.togethersafe.app.test.feature

import android.location.Location
import androidx.activity.viewModels
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.togethersafe.app.test.module.FakeNetworkModule.LATITUDE
import com.togethersafe.app.test.module.FakeNetworkModule.LONGITUDE
import com.togethersafe.app.test.setup.BaseTest
import com.togethersafe.app.ui.viewmodel.MapViewModel
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.checkLocationPermission
import com.togethersafe.app.utils.getCurrentLocation
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Test

@HiltAndroidTest
class UserTrackingTest : BaseTest() {

    private val mapViewModel by lazy { composeTestRule.activity.viewModels<MapViewModel>().value }
    private val userTrackButton by lazy { composeTestRule.onNodeWithTag("UserTrackButton") }

    @Test
    fun tracking() {
        mockkStatic(::checkLocationPermission)
        mockkStatic(::getCurrentLocation)

        every { checkLocationPermission(any()) } returns true
        every { getCurrentLocation(any(), any()) } answers {
            val onLocationReceived = secondArg<(Location) -> Unit>()
            val location = Location("gps")
            location.latitude = LATITUDE
            location.longitude = LONGITUDE
            onLocationReceived(location)
        }

        userTrackButton.performClick()
        composeTestRule.mainClock.advanceTimeBy(1000)

        assertEquals(mapViewModel.userPosition.value, mapViewModel.cameraPosition.value)
        assertEquals(ZOOM_DEFAULT, mapViewModel.zoomLevel.value, 0.0)
    }

}
