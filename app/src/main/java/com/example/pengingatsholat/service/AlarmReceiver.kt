package com.example.pengingatsholat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pengingatsholat.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val namaSholat = intent.getStringExtra("nama_sholat") ?: "Waktu Sholat"

        // 1. Tampilkan Notifikasi
        showNotification(context, namaSholat)

        // 2. Mainkan suara adzan
        val mediaPlayer = MediaPlayer.create(context, R.raw.adzan)
        mediaPlayer?.start()
    }

    private fun showNotification(context: Context, title: String) {
        val channelId = "sholat_channel_id"
        val channelName = "Pengingat Sholat"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Untuk Android O ke atas, buat Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifikasi untuk jadwal sholat"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Waktu Sholat")
            .setContentText("Sudah masuk waktu $title, ayo sholat!")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti sesuai icon kamu
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(title.hashCode(), notification)
    }
}
