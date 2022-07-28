package com.kazuki.fakecameraclient.util.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Composable
fun rememberPermissionState(permission: String) : PermissionState {
    return rememberMutablePermissionState(permission)
}

@Stable
interface PermissionState {
    val permission: String
    val hasPermission: Boolean
    val shouldShowRationale: Boolean
    val permissionRequested: Boolean

    fun launchPermissionRequest(): Unit
}