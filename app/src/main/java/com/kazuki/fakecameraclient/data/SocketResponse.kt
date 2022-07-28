package com.kazuki.fakecameraclient.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class SocketResponse(rawRes: String) {
    var command: String
//    var status: Int
    var data: JSONObject

    init {
        val json = JSONObject(rawRes)
        command = json.getString("command")
//        status = json.getInt("status")
        data = json.getJSONObject("data")
    }

//    fun isEqualTo(command: String, status: Int): Boolean {
//        return "${this.command}${this.status}".compareTo("$command$status") == 0
//    }

    override fun toString(): String {
        return "SocketRequest[$command]"
    }

    companion object {
        fun fromSocket(socket: Socket): SocketResponse? {
            val inputStream = socket.getInputStream()
            val isr = InputStreamReader(inputStream)
            val br = BufferedReader(isr)
            val res = br.readLine()
            br.close()
            Log.d("res", res)
            try {
                return SocketResponse(res)
            } catch (e: JSONException) {
                Log.d("fromSocket", e.toString())
            }
            return null
        }
    }
}