package com.pemmob.wishare.layanan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * Foreground Service untuk menjaga transfer tetap berjalan di latar belakang (FT-13)
 */
class LayananTransfer : Service() {

    private val CHANNEL_ID = "TransferDataChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        buatNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notifikasi = buatNotifikasi("Transfer sedang berjalan...", 0)
        startForeground(NOTIFICATION_ID, notifikasi)
        
        // Disini harusnya ada logika Socket Manager untuk File Transfer
        // Kita jadikan STICKY agar OS mencoba menghidupkannya lagi jika dibunuh
        return START_STICKY
    }

    private fun buatNotifikasi(teks: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WiShare - Transfer File")
            .setContentText(teks)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun buatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Transfer File Background",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
