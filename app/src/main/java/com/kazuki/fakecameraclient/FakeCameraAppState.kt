package com.kazuki.fakecameraclient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.kazuki.fakecameraclient.data.NavigationCommand
import com.kazuki.fakecameraclient.util.NavigationManager
import com.kazuki.fakecameraclient.util.permission.PermissionManager

object NavigationDirections {
    val Empty = object : NavigationCommand {
        override val args: List<NamedNavArgument> = emptyList<NamedNavArgument>()
        override val route: String = ""

    }
    val Home = object : NavigationCommand {
        override val args: List<NamedNavArgument> = emptyList<NamedNavArgument>()
        override val route: String = "home"
    }
    val Camera = object : NavigationCommand {
        override val args: List<NamedNavArgument> = emptyList<NamedNavArgument>()
        override val route: String = "camera"
    }
}

@Composable
fun rememberFakeCameraAppState(
    navController: NavHostController = rememberNavController(),
    navManager: NavigationManager = NavigationManager(),
    permissionManager: PermissionManager
) =
    remember(navController, navManager, permissionManager) {
        FakeCameraAppState(navController, navManager,permissionManager)
    }

class FakeCameraAppState constructor(
    val navController: NavHostController,
    val navManager: NavigationManager,
    val permissionManager: PermissionManager
) {
}