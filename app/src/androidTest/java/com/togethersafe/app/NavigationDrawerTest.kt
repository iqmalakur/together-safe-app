package com.togethersafe.app

import androidx.activity.viewModels
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.togethersafe.app.data.model.User
import com.togethersafe.app.ui.viewmodel.AppViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NavigationDrawerTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val appViewModel by lazy { composeTestRule.activity.viewModels<AppViewModel>().value }
    private val root by lazy { composeTestRule.onRoot() }

    private val menuButton by lazy { composeTestRule.onNodeWithTag("MenuButton") }
    private val welcomeMessage by lazy { composeTestRule.onNodeWithTag("WelcomeMessage") }
    private val userProfileDefault by lazy {
        composeTestRule.onNodeWithTag("UserProfileDefault")
    }
    private val userProfile by lazy { composeTestRule.onNodeWithTag("UserProfile") }
    private val userName by lazy { composeTestRule.onNodeWithTag("UserName") }
    private val userEmail by lazy { composeTestRule.onNodeWithTag("UserEmail") }

    private val name = "Arthur"
    private val email = "arthur@example.com"
    private val profilePhoto = "https://picsum.photos/200"

    @Test
    fun openCloseDrawer() {
        assertFalse(appViewModel.isMenuOpen.value)

        menuButton.performClick()
        assertTrue(appViewModel.isMenuOpen.value)

        composeTestRule.onNodeWithTag("DrawerBackButton").performClick()
        assertFalse(appViewModel.isMenuOpen.value)
    }

    @Test
    fun swipeToCloseDrawer() {
        menuButton.performClick()
        assertTrue(appViewModel.isMenuOpen.value)

        root.performTouchInput { swipe(centerRight, centerLeft) }
        assertFalse(appViewModel.isMenuOpen.value)
    }

    @Test
    fun clickOutsideToCloseDrawer() {
        menuButton.performClick()
        assertTrue(appViewModel.isMenuOpen.value)

        root.performTouchInput { click(Offset(width - 10f, centerY)) }
        composeTestRule.waitForIdle()
        assertFalse(appViewModel.isMenuOpen.value)
    }

    @Test
    fun defaultUserIdentity() {
        menuButton.performClick()

        userProfileDefault.assertExists()
        userProfile.assertDoesNotExist()

        welcomeMessage.assertExists()
        userName.assertDoesNotExist()
        userEmail.assertDoesNotExist()
    }

    @Test
    fun providedUserIdentity() {
        appViewModel.setUser(User(
            name = name,
            email = email,
            profilePhoto = profilePhoto,
        ))

        menuButton.performClick()

        userProfileDefault.assertDoesNotExist()
        userProfile.assertExists()

        welcomeMessage.assertDoesNotExist()
        userName
            .assertExists()
            .assertTextEquals(name)
        userEmail
            .assertExists()
            .assertTextEquals(email)

        appViewModel.setUser(null)
    }

    @Test
    fun providedUserIdentityWithoutProfilePhoto() {
        appViewModel.setUser(User(
            name = name,
            email = email,
            profilePhoto = null,
        ))

        menuButton.performClick()

        userProfileDefault.assertExists()
        userProfile.assertDoesNotExist()

        welcomeMessage.assertDoesNotExist()
        userName
            .assertExists()
            .assertTextEquals(name)
        userEmail
            .assertExists()
            .assertTextEquals(email)

        appViewModel.setUser(null)
    }

}