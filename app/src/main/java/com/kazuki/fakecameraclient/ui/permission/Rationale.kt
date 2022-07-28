package com.kazuki.fakecameraclient.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Rationale(
    onRequestPermission: () -> Unit,
    onDoNotShowRationale: (() -> Unit)? = null
) {
    Column() {
        Text("The camera is important for this app. Please grant the permission.")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = onRequestPermission) {
                Text("Request permission")
            }
            Spacer(Modifier.width(8.dp))
            if (onDoNotShowRationale != null)
                Button(onClick = onDoNotShowRationale) {
                    Text("Don't show rationale again")
                }
        }
    }
}