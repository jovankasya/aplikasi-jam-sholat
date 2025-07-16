package com.example.pengingatsholat.api

import com.example.pengingatsholat.model.SholatResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SholatApiService {
    @GET("timings")
    fun getJadwalSholat(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("method") method: Int = 2
    ): Call<SholatResponse>
}
