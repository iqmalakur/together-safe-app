package com.togethersafe.app.test.feature

import androidx.activity.viewModels
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mapbox.geojson.Point
import com.togethersafe.app.di.NetworkModule
import com.togethersafe.app.test.module.FakeNetworkModule
import com.togethersafe.app.test.setup.BaseTest
import com.togethersafe.app.ui.viewmodel.MapViewModel
import com.togethersafe.app.utils.MapConfig.LATITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.LONGITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class MapHeaderTest : BaseTest() {

    private val searchKeyword = "Testing"
    private val mapViewModel by lazy { composeTestRule.activity.viewModels<MapViewModel>().value }

    private val searchBar by lazy { composeTestRule.onNodeWithTag("SearchBar") }
    private val searchBackButton by lazy {
        composeTestRule.onNodeWithTag("SearchBackButton")
    }
    private val searchNotFoundBox by lazy {
        composeTestRule.onNodeWithTag("SearchNotFoundBox")
    }
    private val menuButton by lazy { composeTestRule.onNodeWithTag("MenuButton") }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun searchBarActiveAndClose() {
        isSearchNonActive()
        searchBar.performClick()
        isSearchActive()
        isSearchNotFound()
        searchBackButton.performClick()
        isSearchNonActive()
    }

    @Test
    fun searching() {
        assertEquals(
            Point.fromLngLat(LONGITUDE_DEFAULT, LATITUDE_DEFAULT),
            mapViewModel.cameraPosition.value
        )

        isSearchNonActive()
        searchBar.performClick()
        isSearchActive()
        isSearchNotFound()

        searchBar.performTextInput(searchKeyword)
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("Name-Item-0", useUnmergedTree = true)
            .assertExists()
            .assertTextEquals(searchKeyword)
        composeTestRule.onNodeWithTag("DisplayName-Item-0", useUnmergedTree = true)
            .assertExists()
            .assertTextEquals(searchKeyword)

        composeTestRule.onNodeWithTag("Row-Item-0", useUnmergedTree = true)
            .assertExists()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(ZOOM_DEFAULT, mapViewModel.zoomLevel.value, 0.0)
        assertEquals(
            Point.fromLngLat(FakeNetworkModule.LONGITUDE, FakeNetworkModule.LATITUDE),
            mapViewModel.cameraPosition.value
        )
    }

    @Test
    fun searchNotFound() {
        isSearchNonActive()
        searchBar.performClick()
        isSearchActive()
        isSearchNotFound()

        searchBar.performTextInput("Test")
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        isSearchNotFound()
    }

    private fun isSearchNotFound() {
        searchNotFoundBox.assertExists()
    }

    private fun isSearchActive() {
        menuButton.assertDoesNotExist()
        searchBackButton.assertExists()
    }

    private fun isSearchNonActive() {
        menuButton.assertExists()
        searchBackButton.assertDoesNotExist()
    }

}