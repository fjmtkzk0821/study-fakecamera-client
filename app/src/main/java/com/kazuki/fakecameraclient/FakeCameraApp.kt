package com.kazuki.fakecameraclient

import android.Manifest
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kazuki.fakecameraclient.repos.SharedPrefRepository
import com.kazuki.fakecameraclient.ui.*
import com.kazuki.fakecameraclient.ui.permission.PermissionDenied
import com.kazuki.fakecameraclient.util.NavCommands
import com.kazuki.fakecameraclient.util.permission.PermissionManager
import com.kazuki.fakecameraclient.util.permission.rememberPermissionManager
import com.kazuki.fakecameraclient.util.permission.rememberPermissionState

@ExperimentalAnimationApi
@Composable
fun FakeCameraApp(
    appState: FakeCameraAppState = rememberFakeCameraAppState(
        permissionManager = rememberPermissionManager(
            permissions = listOf(
                Manifest.permission.CAMERA
            )
        )
    )
) {
    appState.navManager.commands.collectAsState().value.also { command ->
        if (command.route.isNotEmpty())
            appState.navController.navigate(command.route)
    }
    PermissionsRequired(
        permissionManager = appState.permissionManager,
        whileNotGrantedContent = { Rationale(onRequestPermission = { appState.permissionManager.launchPermissionsRequest() }) },
        whileNotAvailableContent = { PermissionDenied() }) {
        NavHost(
            navController = appState.navController,
            startDestination = NavigationDirections.Home.route
        ) {
            composable(NavigationDirections.Home.route) { navBackStackEntry ->
                Home(appState = appState)
            }
            composable(NavCommands.Camera.route) { navBackStackEntry ->
                val ip = navBackStackEntry.arguments?.getString("ip")
                FakeCamera(
                    appState = appState,
                    viewModel = FakeCameraViewModel(
                        appState,
                        SharedPrefRepository(LocalContext.current), ip
                    )
                )
            }
        }
    }
}