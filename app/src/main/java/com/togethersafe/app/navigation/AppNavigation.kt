package com.togethersafe.app.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.views.map.MapScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides  navController) {
        NavHost(navController = navController, startDestination = "map") {
            composable("map") { MapScreen() }
//            composable("login") { LoginScreen() }
        }
    }
}
