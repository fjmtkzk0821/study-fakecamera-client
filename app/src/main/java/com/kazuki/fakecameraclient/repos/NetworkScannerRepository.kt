package com.kazuki.fakecameraclient.repos

import android.util.Log
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

class NetworkScannerRepository {
    suspend fun retrieveReachableHosts(): MutableList<InetAddress> = coroutineScope {
        val prefix = getPrivateIpAddressPrefix()
        val list = mutableListOf<InetAddress>()
        val jobs = mutableListOf<Job>()
        for(i in 1..254) {
            jobs += launch {
                val addr = InetAddress.getByName("$prefix.$i")
                if(addr.isReachable(200)) {
                    Log.d("FIND", "IP retrieved [${addr.hostAddress?:"null"}]")
                    list.add(addr)
                }
            }
        }
        jobs.forEach { it.join() }
        return@coroutineScope list
    }

    fun getPrivateIpAddressPrefix(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enIpAddr = intf.inetAddresses
                while (enIpAddr.hasMoreElements()) {
                    val inetAddr = enIpAddr.nextElement()
                    if(!inetAddr.isLoopbackAddress&&inetAddr is Inet4Address) {
                        val ipAddr = inetAddr.hostAddress.substringBeforeLast(".")
                        Log.e("IP address",""+ipAddr)
                        return ipAddr
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
        }
        return null
    }
}