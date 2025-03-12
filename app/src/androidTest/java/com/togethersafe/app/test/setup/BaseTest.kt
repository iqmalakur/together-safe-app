package com.togethersafe.app.test.setup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.togethersafe.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule

abstract class BaseTest {

    @get:Rule
    val hiltRule by lazy { HiltAndroidRule(this) }

    @get:Rule
    val composeTestRule by lazy { createAndroidComposeRule<MainActivity>() }

}
