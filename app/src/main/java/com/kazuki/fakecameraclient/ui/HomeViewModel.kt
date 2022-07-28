package com.kazuki.fakecameraclient.ui

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazuki.fakecameraclient.data.*
import com.kazuki.fakecameraclient.repos.NetworkScannerRepository
import com.kazuki.fakecameraclient.repos.SharedPrefRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket

class HomeViewModel constructor(
    private val context: Context,
    private val sharedPrefRepo: SharedPrefRepository,
    private val networkScannerRepo: NetworkScannerRepository = NetworkScannerRepository(),
) : ViewModel() {
    var inputCode by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var alert: Alert by mutableStateOf(AlertPrefab.None)

    fun startPairing(onSuccess: (value: String?) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val deviceName = Settings.Secure.getString(
            context.contentResolver,
            "bluetooth_name"
        )
        isLoading = true
        var isServerFound = false
        val hosts = networkScannerRepo.retrieveReachableHosts()
        for (host in hosts) {
            Log.d("FIND", "socket pairing [${host.hostAddress}]")
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(host.hostAddress ?: "", 6767), 5000)
                socket.soTimeout = 5000
                if (socket.isConnected) {
                    var request = SocketRequest(
                        "PAIR",
                        JSONObject(
                            mapOf(
                                "code" to inputCode,
                                "device" to deviceName
                            )
                        )
                    )
                    var isWaiting = true
                    request.send(socket)
                    Log.d("PAIR", "socket connected [${host.hostAddress}]")
                    do {
                        val response = SocketResponse.fromSocket(socket)
                        if (response != null) {
                            isWaiting = false
                            socket.close()
                            if (response.data.has("status") && response.data["status"] == 200) {
//                                sharedPrefRepo.put("secret", response.data["secret"] as String)
                                Log.d("PAIR", "success [${host.hostAddress}]")
                                isServerFound = true
                                onSuccess(host.hostAddress)
                            }
                        }
                    } while (isWaiting)
                }
            } catch (ex: Exception) {
                Log.e("pair", ex.toString())
            }
        }
        isLoading = false
        if (!isServerFound)
            alert = Alert(
                type = AlertType.WARNING,
                title = "No server was found",
                detail = "Maybe forgot to launch the server app?",
                confirmAction = Pair("I'll have a look", {
                    alert = AlertPrefab.None
                })
            )
    }
}