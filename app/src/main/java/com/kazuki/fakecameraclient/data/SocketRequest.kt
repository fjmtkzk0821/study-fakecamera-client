package com.kazuki.fakecameraclient.data

import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.lang.Exception
import java.net.*
import java.nio.ByteBuffer

class SocketRequest(var command: String, var data: JSONObject? = null) {

    fun send(socket: Socket) {
        try {
            val outputStream = socket.getOutputStream()
            val bos = BufferedOutputStream(outputStream)
            var rawData = toJSON().toString().toByteArray()
            var bb = ByteBuffer.allocate(4+rawData.size)
            bb.putInt(rawData.size)
            bb.put(rawData)
            bos.write(bb.array())
            bos.flush()
//            outputStream.flush()
//        outputStream.close()
        } catch (ex: Exception) {
            Log.e("socket[send]", ex.toString())
        }
    }

    private fun toJSON(): JSONObject {
        var obj = JSONObject()
        obj.put("command", command)
        if(data != null)
            obj.put("data", data)
        return obj
    }

    override fun toString(): String {
        return "SocketRequest[$command]"
    }
}