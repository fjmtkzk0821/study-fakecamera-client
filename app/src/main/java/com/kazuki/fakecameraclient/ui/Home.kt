package com.kazuki.fakecameraclient.ui

import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazuki.fakecameraclient.FakeCameraAppState
import com.kazuki.fakecameraclient.data.AlertType
import com.kazuki.fakecameraclient.repos.SharedPrefRepository
import com.kazuki.fakecameraclient.ui.components.DefaultAlertDialog
import com.kazuki.fakecameraclient.util.NavCommands
import com.kazuki.fakecameraclient.util.clearFocus

@Composable
fun Home(
    appState: FakeCameraAppState,
    viewModel: HomeViewModel = HomeViewModel(
        LocalContext.current,
        SharedPrefRepository(LocalContext.current)
    )
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .clearFocus(LocalFocusManager.current)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "icon",
                    modifier = Modifier.size(150.dp, 150.dp)
                )
                Box(modifier = Modifier.height(64.dp))
                if (viewModel.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column() {
                        Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 32.sp),
                            value = viewModel.inputCode,
                            onValueChange = { viewModel.inputCode = it },
                            label = { Text("Pairing Code", fontSize = 18.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Box(modifier = Modifier.height(32.dp))
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp), onClick = {
                            viewModel.startPairing { ip ->
                                Log.d("Pairing", "success [ip: $ip]")
                                appState.navManager.navigate(NavCommands.Camera.get(ip))
//                appState.navController.navigate()
                            }
                        }) {
                            Text("Start Pairing")
                        }
                    }
                }
            }
        }
        DefaultAlertDialog(viewModel.alert)
    }
}