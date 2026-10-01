package com.pemmob.wishare.data.model

data class ProgresTransfer(
    val idFile: String = "",
    val namaFile: String = "",
    val bytesTerkirim: Long = 0,
    val totalBytes: Long = 0,
    val sedangBerjalan: Boolean = false,
    val selesai: Boolean = false,
    val dibatalkan: Boolean = false
) {
    val persentase: Float
        get() = if (totalBytes > 0) (bytesTerkirim.toFloat() / totalBytes.toFloat()) else 0f
}
