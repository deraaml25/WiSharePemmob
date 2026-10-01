package com.pemmob.wishare.inti.jaringan

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * Mengelola koneksi Soket TCP (FT-04) dan Full-Duplex Chat/Transfer (FT-05)
 */
class PengelolaSoket {
    private val PORT = 8988
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    
    private val _statusKoneksi = MutableStateFlow("Terputus")
    val statusKoneksi: StateFlow<String> = _statusKoneksi.asStateFlow()

    private val _pesanMasuk = MutableStateFlow<String>("")
    val pesanMasuk: StateFlow<String> = _pesanMasuk.asStateFlow()

    // Antrean pengiriman agar thread-safe
    private val antreanKirim = Channel<FramePesan>(Channel.UNLIMITED)

    suspend fun mulaiSebagaiServer() {
        withContext(Dispatchers.IO) {
            try {
                _statusKoneksi.value = "Menunggu Klien..."
                serverSocket = ServerSocket(PORT).apply { reuseAddress = true }
                socket = serverSocket?.accept() // Blokir sampai klien masuk
                
                _statusKoneksi.value = "Terhubung (Sebagai Server)"
                mulaiSesiKomunikasi()
            } catch (e: java.io.IOException) {
                Log.e("PengelolaSoket", "Error Server: ${e.message}")
                _statusKoneksi.value = "Gagal Terhubung"
                putuskan()
            }
        }
    }

    suspend fun mulaiSebagaiKlien(hostAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                _statusKoneksi.value = "Menghubungkan ke $hostAddress..."
                socket = Socket()
                // Coba hubungkan dengan timeout 5 detik
                socket?.connect(InetSocketAddress(hostAddress, PORT), 5000)
                
                _statusKoneksi.value = "Terhubung (Sebagai Klien)"
                mulaiSesiKomunikasi()
            } catch (e: java.io.IOException) {
                Log.e("PengelolaSoket", "Error Klien: ${e.message}")
                _statusKoneksi.value = "Gagal Terhubung"
                putuskan()
            }
        }
    }

    /**
     * Loop baca-tulis full-duplex (FT-05)
     */
    private suspend fun mulaiSesiKomunikasi() = coroutineScope {
        val s = socket ?: return@coroutineScope
        val inStream = s.getInputStream()
        val outStream = s.getOutputStream()

        // Coroutine untuk Membaca
        launch(Dispatchers.IO) {
            try {
                while (isActive && !s.isClosed) {
                    val frame = FramePesan.bacaDariStream(inStream)
                    tanganiPesanMasuk(frame)
                }
            } catch (e: Exception) {
                Log.e("PengelolaSoket", "Soket Terputus (Baca): ${e.message}")
                putuskan()
            }
        }

        // Coroutine untuk Menulis dari Antrean
        launch(Dispatchers.IO) {
            try {
                for (pesan in antreanKirim) {
                    if (s.isClosed) break
                    pesan.tulisKeStream(outStream)
                }
            } catch (e: Exception) {
                Log.e("PengelolaSoket", "Soket Terputus (Tulis): ${e.message}")
                putuskan()
            }
        }
    }

    private fun tanganiPesanMasuk(frame: FramePesan) {
        when (frame.tipe) {
            TipePesan.TEKS -> {
                val teks = String(frame.payload, Charsets.UTF_8)
                _pesanMasuk.value = teks
            }
            // TODO: Tangani penerimaan file (FT-09)
            else -> {}
        }
    }

    fun kirimPesanTeks(teks: String) {
        antreanKirim.trySend(FramePesan.buatPesanTeks(teks))
    }

    fun putuskan() {
        try {
            socket?.close()
            serverSocket?.close()
        } catch (e: Exception) {}
        finally {
            _statusKoneksi.value = "Terputus"
        }
    }
}
