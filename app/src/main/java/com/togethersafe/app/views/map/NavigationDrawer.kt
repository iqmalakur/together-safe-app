package com.togethersafe.app.views.map

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.togethersafe.app.data.model.User
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun NavigationDrawer(screenContent: @Composable () -> Unit) {
    val appViewModel: AppViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val isMenuOpen by appViewModel.isMenuOpen.collectAsState()

    LaunchedEffect(isMenuOpen) {
        if (isMenuOpen) drawerState.open()
        else drawerState.close()
    }

    // Handle close the menu if the drawer is closed by user gesture (swipe)
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.currentValue }
            .collect { state ->
                if (state == DrawerValue.Closed && isMenuOpen) {
                    appViewModel.setMenuOpen(false)
                }
            }
    }

    if (isMenuOpen) {
        BackHandler { appViewModel.setMenuOpen(false) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMenuOpen,
        drawerContent = { DrawerContent() },
    ) {
        Box { screenContent() }
    }
}

@Composable
private fun DrawerContent() {
    val appViewModel: AppViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
    val navController = LocalNavController.current

    val user by appViewModel.user.collectAsState()

    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxSize()) {
            DrawerHeader()

            DrawerItem("Akun Saya") { /* TODO: Handle action */ }
            DrawerItem("Tambah Laporan") { /* TODO: Handle action */ }
            DrawerItem("Laporan Saya") { /* TODO: Handle action */ }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    appViewModel.setMenuOpen(false)
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    if (user == null) "Login"
                    else "Logout"
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    val appViewModel: AppViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
    val user by appViewModel.user.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserProfile(user)
        Spacer(Modifier.width(8.dp))
        UserIdentity(user)

        Spacer(Modifier.weight(1f))
        IconButton(
            modifier = Modifier.testTag("DrawerBackButton"),
            onClick = { appViewModel.setMenuOpen(false) }
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup Drawer")
        }
    }

    HorizontalDivider()
}

@Composable
private fun DrawerItem(label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text = label) },
        selected = false,
        onClick = onClick
    )
}

@Composable
private fun UserProfile(user: User?) {
    if (user?.profilePhoto != null) {
        AsyncImage(
            model = user.profilePhoto,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .testTag("UserProfile")
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .testTag("UserProfileDefault")
                .size(48.dp)
        )
    }
}

@Composable
private fun UserIdentity(user: User?) {
    Column {
        if (user != null) {
            Text(
                modifier = Modifier.testTag("UserName"),
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.testTag("UserEmail"),
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                modifier = Modifier.testTag("WelcomeMessage"),
                text = "Selamat Datang",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
