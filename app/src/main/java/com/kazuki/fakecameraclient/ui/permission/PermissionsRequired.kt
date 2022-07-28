package com.kazuki.fakecameraclient.ui

import androidx.compose.runtime.Composable
import com.kazuki.fakecameraclient.util.permission.PermissionManager

@Composable
fun PermissionsRequired(
    permissionManager: PermissionManager,
    whileNotGrantedContent: @Composable (() -> Unit),
    whileNotAvailableContent: @Composable (() -> Unit),
    content: @Composable (() -> Unit)
) {
    when {
        permissionManager.allPermissionGranted -> {
            content()
        }
        permissionManager.shouldShowRationale || !permissionManager.permissionRequested -> {
            whileNotGrantedContent()
        }
        else -> {
            whileNotAvailableContent()
        }
    }
}