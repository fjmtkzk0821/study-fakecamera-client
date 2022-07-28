package com.kazuki.fakecameraclient.util.permission

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.lang.IllegalStateException

@Composable
internal fun rememberMutablePermissionsState(permissions: List<String>): List<MutablePermissionState> {
    return permissions.map { rememberMutablePermissionState(it) }
}

@Composable
internal fun rememberMutablePermissionState(
    permission: String
): MutablePermissionState {
    val context = LocalContext.current
    val permissionState = remember(permission) {
        MutablePermissionState(context,permission)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.hasPermission = it
        permissionState.permissionRequested = true
    }

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }
    return permissionState
}

class MutablePermissionState(
    private val context: Context,
    override val permission: String,
) : PermissionState {
    private val activity: Activity = context.findActivity()
    private var _hasPermission by mutableStateOf(context.checkPermission(permission))

    override var hasPermission: Boolean
        internal set(value) {
            _hasPermission = value
            refreshShouldShowRationale()
        }
        get() = _hasPermission

    override var shouldShowRationale: Boolean by mutableStateOf(
        activity.shouldShowRationale(
            permission
        )
    )
        private set

    override var permissionRequested: Boolean by mutableStateOf(false)

    override fun launchPermissionRequest() {
        launcher?.launch(permission)
            ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    internal fun refreshHasPermission() {
        hasPermission = context.checkPermission(permission)
    }

    private fun refreshShouldShowRationale() {
        shouldShowRationale = activity.shouldShowRationale(permission)
    }
}