package com.kazuki.fakecameraclient.ui

import android.graphics.Bitmap
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazuki.fakecameraclient.util.padLeft
import com.kazuki.fakecameraclient.util.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kazuki.fakecameraclient.FakeCameraAppState
import com.kazuki.fakecameraclient.NavigationDirections
import com.kazuki.fakecameraclient.data.*
import com.kazuki.fakecameraclient.repos.SharedPrefRepository
import org.json.JSONObject
import java.lang.Exception
import java.net.*

class FakeCameraViewModel constructor(
    private val appState: FakeCameraAppState,
    private val sharedPrefRepo: SharedPrefRepository,
    val ip: String?
) :
    ViewModel() {
    private var socket: Socket = Socket()
    private val address = InetSocketAddress(ip, 6767)
    private var secret = ""
    var lock: Boolean = false
    var captureInterrupt = false
    private val queue: Queue<Bitmap> = LinkedList<Bitmap>()
    var alert: Alert by mutableStateOf(AlertPrefab.None)

//    init {
//        secret = sharedPrefRepo.getString("secret")
//    }

    private fun closeConnection() {
        if (socket.isConnected && !socket.isClosed) {
            socket.shutdownOutput()
            socket.close()
        }
    }

    fun wait4ServerSignal() = viewModelScope.launch(Dispatchers.IO) {
        var isContinueLoop = false
        try {
            var s = Socket()
            do {
                s.connect(InetSocketAddress(ip, 6767), 1000)
                val response = SocketResponse.fromSocket(s)
                if (response != null) {
                    s.close()
                    when (response.command) {
                        "DISC" -> {
                            appState.navManager.navigate(NavigationDirections.Home)
                        }
                        else -> {
                            appState.navManager.navigate(NavigationDirections.Home)
                        }
                    }
                }
            } while (isContinueLoop)
        } catch (ex: ConnectException) {

        }
    }

    fun sendBitmap(bitmap: Bitmap) {
        if (captureInterrupt) return
        queue.offer(bitmap)
        viewModelScope.launch(Dispatchers.IO) {
            if (lock) return@launch
            lock = true
            var frame = queue.poll()
            if (frame != null) {
                try {
                    socket = Socket()
                    socket.connect(address, 15 * 1000)
                    socket.soTimeout = 15 * 1000
                    socket.tcpNoDelay = true
//                    val response = SocketResponse.fromSocket(socket)
//                    if (response != null) {
//                        when (response.command) {
//                            "DISC" -> {
//                                captureInterrupt = true
//                                closeConnection()
//                                appState.navManager.navigate(NavigationDirections.Home)
//                            }
//                        }
//                    }
                    val baos = ByteArrayOutputStream()
                    val onWrite = async {
                        val request = SocketRequest("COMU")
                        frame = frame.scale(0.5f)
                        frame.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                        request.data = JSONObject(
                            mapOf(
                                "frame" to Base64.encodeToString(
                                    baos.toByteArray(),
                                    Base64.DEFAULT
                                )
                            )
                        )
                        request.send(socket)
                    }
                    onWrite.await()
                    closeConnection()
                } catch (ex: SocketTimeoutException) {
//                    alert = AlertPrefab.None
//                    closeConnection()
//                    alert = Alert(
//                        type = AlertType.ERROR,
//                        title = "Connection Timeout",
//                        detail = "take too long while connect to server",
//                        confirmAction = Pair("Back to menu", {
//                            alert = AlertPrefab.None
//                            returnHome()
//                        }),
//                    )
                } catch (ex: SocketException) {
                    captureInterrupt = true
                    alert = AlertPrefab.None
                    closeConnection()
                    alert = Alert(
                        type = AlertType.ERROR,
                        title = "Connection Reset",
                        detail = "Connection reset while write",
                        confirmAction = Pair("Back to menu", {
                            alert = AlertPrefab.None
                            returnHome()
                        })
                    )
                }
            }
            lock = false
        }
    }

    fun onBackPress() {
        alert = Alert(
            type = AlertType.WARNING,
            title = "Confirm to leave?",
            detail = "Connection will be dispose",
            dismissAction = Pair("Stay", {
                alert = AlertPrefab.None
            }),
            confirmAction = Pair("Leave", {
                alert = AlertPrefab.None
                returnHome()
            })
        )
    }

    private fun returnHome() {
        closeConnection()
        appState.navManager.navigate(NavigationDirections.Home)
    }
}