package com.example.pengingatsholat.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            // Di sini bisa kamu aktifkan ulang alarm jika perlu
            Toast.makeText(context, "Perangkat menyala - siap mengatur ulang alarm", Toast.LENGTH_SHORT).show()

            // TODO: Ambil data alarm yang tersimpan dan aktifkan ulang di sini
        }
    }
}
