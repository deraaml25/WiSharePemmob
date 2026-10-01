package com.pemmob.wishare.inti.jaringan

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Mendefinisikan tipe pesan dalam komunikasi soket (FT-08)
 */
enum class TipePesan(val kode: Byte) {
    TEKS(1),
    METADATA_FILE(2),
    CHUNK_FILE(3),
    SELESAI(4);

    companion object {
        fun dariKode(kode: Byte): TipePesan = values().firstOrNull { it.kode == kode } ?: TEKS
    }
}

/**
 * Model Frame Protokol: [Tipe (1 byte)] + [Panjang (4 byte)] + [Payload (N byte)]
 */
data class FramePesan(
    val tipe: TipePesan,
    val payload: ByteArray
) {
    fun tulisKeStream(outStream: OutputStream) {
        val panjang = payload.size
        val header = ByteBuffer.allocate(5)
            .put(tipe.kode)
            .putInt(panjang)
            .array()
        
        outStream.write(header)
        if (panjang > 0) {
            outStream.write(payload)
        }
        outStream.flush()
    }

    companion object {
        fun bacaDariStream(inStream: InputStream): FramePesan {
            val headerBuffer = ByteArray(5)
            var bytesRead = 0
            while (bytesRead < 5) {
                val read = inStream.read(headerBuffer, bytesRead, 5 - bytesRead)
                if (read == -1) throw Exception("Stream terputus saat membaca header")
                bytesRead += read
            }

            val buffer = ByteBuffer.wrap(headerBuffer)
            val tipeByte = buffer.get()
            val panjang = buffer.getInt()

            val tipe = TipePesan.dariKode(tipeByte)
            val payload = ByteArray(panjang)
            
            var payloadRead = 0
            while (payloadRead < panjang) {
                val read = inStream.read(payload, payloadRead, panjang - payloadRead)
                if (read == -1) throw Exception("Stream terputus saat membaca payload")
                payloadRead += read
            }

            return FramePesan(tipe, payload)
        }
        
        fun buatPesanTeks(teks: String): FramePesan {
            return FramePesan(TipePesan.TEKS, teks.toByteArray(StandardCharsets.UTF_8))
        }
    }
}
