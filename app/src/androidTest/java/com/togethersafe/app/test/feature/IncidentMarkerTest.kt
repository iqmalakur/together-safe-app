package com.togethersafe.app.test.feature

import androidx.activity.viewModels
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import com.togethersafe.app.test.setup.BaseTest
import com.togethersafe.app.ui.viewmodel.IncidentViewModel
import com.togethersafe.app.ui.viewmodel.MapViewModel
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class IncidentMarkerTest : BaseTest() {

    private val mapViewModel by lazy { composeTestRule.activity.viewModels<MapViewModel>().value }
    private val incidentViewModel by lazy {
        composeTestRule.activity.viewModels<IncidentViewModel>().value
    }

    private val bottomSheet by lazy { composeTestRule.onNodeWithTag("BottomSheet") }
    private val category by lazy {
        composeTestRule.onNodeWithTag("IncidentDetail-Kategori")
    }
    private val location by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Lokasi") }
    private val date by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Tanggal") }
    private val time by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Jam") }
    private val riskLevel by lazy {
        composeTestRule.onNodeWithTag("IncidentDetail-Tingkat Risiko")
    }
    private val status by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Status") }
    private val reportCount by lazy {
        composeTestRule.onNodeWithTag("IncidentDetail-Jumlah Laporan")
    }
    private val image by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Image-0") }
    private val report by lazy { composeTestRule.onNodeWithTag("IncidentDetail-Report-0") }

    @Test
    fun incidentMarkerDetail() {
        bottomSheet.assertDoesNotExist()

        val incident = incidentViewModel.incidents.value[0]
        mapViewModel.setCameraPosition(incident.latitude, incident.longitude)
        composeTestRule.waitForIdle()

        incidentViewModel.setSelectedIncident(incident)
        composeTestRule.waitForIdle()

        bottomSheet.assertExists()

        category
            .assertExists()
            .assertTextEquals("Testing")

        date
            .assertExists()
            .assertTextEquals("Tanggal: 01 Januari 1970")

        time
            .assertExists()
            .assertTextEquals("Jam: 00:00")

        riskLevel
            .assertExists()
            .assertTextEquals("Tingkat Risiko: high")

        location
            .assertExists()
            .assertTextEquals("Lokasi: Jl. Testing")

        status
            .assertExists()
            .assertTextEquals("Status: active")

        reportCount
            .assertExists()
            .assertTextEquals("Jumlah Laporan: 1")

        image.assertExists()

        report
            .assertExists()
            .assertTextEquals("- lorem ipsum dolor sit amet")

        incidentViewModel.setSelectedIncident(null)
        composeTestRule.waitForIdle()
        bottomSheet.assertDoesNotExist()
    }

}
