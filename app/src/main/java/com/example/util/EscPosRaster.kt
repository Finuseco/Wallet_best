package com.example.util

import java.io.OutputStream

/** Standard ESC/POS raster output. No proprietary commands or automatic cutter. */
object EscPosRaster {
    fun write(output: OutputStream, width: Int, height: Int, pixels: IntArray) {
        require(width in setOf(384,576) && height>0 && pixels.size==width*height)
        output.write(byteArrayOf(0x1b,0x40,0x1b,0x61,0x01))
        val rowBytes=width/8
        for(top in 0 until height step 128) {
            val rows=minOf(128,height-top)
            output.write(byteArrayOf(0x1d,0x76,0x30,0x00,rowBytes.toByte(),(rowBytes shr 8).toByte(),rows.toByte(),(rows shr 8).toByte()))
            val raster=ByteArray(rowBytes*rows)
            for(y in 0 until rows) for(x in 0 until width) {
                val color=pixels[(top+y)*width+x]
                val alpha=(color ushr 24) and 255
                val luminance=(299*((color ushr 16) and 255)+587*((color ushr 8) and 255)+114*(color and 255))/1000
                val onWhite=(luminance*alpha+255*(255-alpha))/255
                if(onWhite<170) {val index=y*rowBytes+x/8;raster[index]=(raster[index].toInt() or (0x80 ushr (x%8))).toByte()}
            }
            output.write(raster)
        }
        output.write(byteArrayOf(0x1b,0x64,0x03))
        output.flush()
    }
}
