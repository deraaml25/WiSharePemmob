package com.pemmob.wishare.data.model

import android.net.wifi.p2p.WifiP2pDevice

enum class StatusPeer {
    TERSEDIA,     // Available (3)
    TERUNDANG,    // Invited (1)
    TERHUBUNG,    // Connected (0)
    GAGAL,        // Failed (2)
    TIDAK_DIKETAHUI // Unavailable (4)
}

data class PerangkatPeer(
    val alamatMac: String,
    val namaPerangkat: String,
    val status: StatusPeer,
    val isGroupOwner: Boolean = false
) {
    companion object {
        fun dariWifiP2pDevice(device: WifiP2pDevice): PerangkatPeer {
            val status = when (device.status) {
                WifiP2pDevice.CONNECTED -> StatusPeer.TERHUBUNG
                WifiP2pDevice.INVITED -> StatusPeer.TERUNDANG
                WifiP2pDevice.FAILED -> StatusPeer.GAGAL
                WifiP2pDevice.AVAILABLE -> StatusPeer.TERSEDIA
                else -> StatusPeer.TIDAK_DIKETAHUI
            }
            return PerangkatPeer(
                alamatMac = device.deviceAddress ?: "",
                namaPerangkat = device.deviceName ?: "Perangkat Tidak Dikenal",
                status = status,
                isGroupOwner = device.isGroupOwner
            )
        }
    }
}
