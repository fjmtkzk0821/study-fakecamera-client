package com.kazuki.fakecameraclient.util.permission

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun rememberPermissionManager(permissions: List<String>): PermissionManager {
    val context = LocalContext.current
    val manager = PermissionManager(context, rememberMutablePermissionsState(permissions))
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        manager.updatePermissionsState(it)
        manager.permissionRequested = true
    }

    DisposableEffect(manager, launcher) {
        manager.launcher = launcher
        onDispose {
            manager.launcher = null
        }
    }
    return manager
}

class PermissionManager(val context: Context, val permissions: List<MutablePermissionState>) {

    //return list of not granted permissions
    val revokedPermissions: List<MutablePermissionState> by derivedStateOf {
        permissions.filter { !it.hasPermission }
    }

    val allPermissionGranted: Boolean by derivedStateOf {
        permissions.all { it.hasPermission } || revokedPermissions.isEmpty()
    }

    val shouldShowRationale: Boolean by derivedStateOf {
        permissions.any {it.shouldShowRationale}
    }

    var permissionRequested: Boolean by mutableStateOf(false)

    fun launchPermissionsRequest() {
        launcher?.launch(
            permissions.map { it.permission }.toTypedArray()
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal var launcher: ActivityResultLauncher<Array<String>>? = null

    internal fun updatePermissionsState(state: Map<String, Boolean>) {
        for (p in state.keys) {
            permissions.firstOrNull { it.permission == p }?.apply {
                state[p]?.let {
                    this.hasPermission = it
                }
            }
        }
    }
}