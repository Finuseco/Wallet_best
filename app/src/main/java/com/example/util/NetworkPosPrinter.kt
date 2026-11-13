package com.example.util

import android.graphics.Bitmap
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

object NetworkPosPrinter {
    /** Only runs following an explicit user action, on Dispatchers.IO. */
    fun print(bitmap: Bitmap, address: String, port: Int) {
        val host=address.trim()
        val parts=host.split('.')
        val ipv4=parts.size==4 && parts.all{part->part.isNotEmpty() && part.all{it in '0'..'9'} && (part.toIntOrNull() ?: -1) in 0..255}
        val ipv6=host.contains(':') && host.matches(Regex("[0-9a-fA-F:.]+"))
        require(ipv4 || ipv6) { "Adresse IP invalide" }
        require(port in 1..65535) { "Port invalide" }
        val pixels=IntArray(bitmap.width*bitmap.height)
        bitmap.getPixels(pixels,0,bitmap.width,0,0,bitmap.width,bitmap.height)
        Socket().use { socket ->
            socket.connect(InetSocketAddress(InetAddress.getByName(host),port),5000)
            socket.soTimeout=5000
            val deadline=java.util.Timer("CashPay POS deadline",true)
            try {
                deadline.schedule(object:java.util.TimerTask(){override fun run(){runCatching{socket.close()}}},15000)
                EscPosRaster.write(socket.getOutputStream(),bitmap.width,bitmap.height,pixels)
            } finally {deadline.cancel()}
        }
    }
}
