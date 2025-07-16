package com.example.pengingatsholat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pengingatsholat.model.JadwalSholat

class SholatViewModel : ViewModel() {
    private val _jadwalSholat = MutableLiveData<List<JadwalSholat>>()
    val jadwalSholat: LiveData<List<JadwalSholat>> = _jadwalSholat

    fun updateJadwal(list: List<JadwalSholat>) {
        _jadwalSholat.value = list
    }
}
