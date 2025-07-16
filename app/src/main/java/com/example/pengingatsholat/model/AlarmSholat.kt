package com.example.pengingatsholat.model

data class AlarmSholat(
    val nama: String,
    val waktu: String,
    var aktif: Boolean = false
)
