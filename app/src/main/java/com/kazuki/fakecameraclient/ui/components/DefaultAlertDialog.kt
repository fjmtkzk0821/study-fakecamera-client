package com.kazuki.fakecameraclient.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.kazuki.fakecameraclient.data.Alert
import com.kazuki.fakecameraclient.data.AlertType

@Composable
fun DefaultAlertDialog(alert: Alert) {
    if (alert.type != AlertType.NONE) {
        AlertDialog(onDismissRequest = {
            if (alert.dismissAction != null)
                alert.dismissAction.second.invoke()
            else
                alert.confirmAction.second()
        }, title = {
            Text(alert.title)
        }, text = {
            Text(alert.detail)
        },
            dismissButton = {
                if (alert.dismissAction != null) {
                    TextButton(onClick = {
                        alert.dismissAction.second.invoke()
                    }) {
                        Text(alert.dismissAction.first)
                    }
                }
            }, confirmButton = {
                Button(onClick = {
                    alert.confirmAction.second()
                }) {
                    Text(alert.confirmAction.first)
                }
            })
    }
}