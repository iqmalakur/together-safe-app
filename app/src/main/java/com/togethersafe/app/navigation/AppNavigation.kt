package com.togethersafe.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.togethersafe.app.views.login.LoginScreen
import com.togethersafe.app.views.map.MapScreen
import com.togethersafe.app.views.register.RegisterScreen
import com.togethersafe.app.views.report.ReportListScreen
import com.togethersafe.app.views.report.ReportScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = "map") {
            composable("map") { MapScreen() }
            composable("login") { LoginScreen() }
            composable("register") { RegisterScreen() }
            composable("report") { ReportScreen() }
            composable("report-list") { ReportListScreen() }
        }
    }
}
