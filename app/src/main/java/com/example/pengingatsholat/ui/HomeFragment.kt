package com.example.pengingatsholat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pengingatsholat.R
import com.example.pengingatsholat.adapter.SholatAdapter
import com.example.pengingatsholat.api.SholatApiService
import com.example.pengingatsholat.databinding.FragmentHomeBinding
import com.example.pengingatsholat.model.JadwalSholat
import com.example.pengingatsholat.model.SholatResponse
import com.example.pengingatsholat.network.RetrofitClient
import com.example.pengingatsholat.viewmodel.SholatViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: SholatAdapter
    private lateinit var viewModel: SholatViewModel

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocationAndFetchPrayerTimes()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        viewModel = ViewModelProvider(requireActivity())[SholatViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        showCurrentDate()
        showCurrentTime()
        checkLocationPermission()

        binding.btnAturAlarm.setOnClickListener {
            findNavController().navigate(R.id.alarmFragment)
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocationAndFetchPrayerTimes()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(requireContext(), "Izin lokasi dibutuhkan untuk menampilkan jadwal", Toast.LENGTH_LONG).show()
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = SholatAdapter()
        binding.rvJadwalSholat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvJadwalSholat.adapter = adapter
    }

    private fun showCurrentDate() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id"))
        val date = dateFormat.format(Date())
        binding.tvDate.text = date
    }

    private fun showCurrentTime() {
        val handler = Handler(Looper.getMainLooper())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        handler.post(object : Runnable {
            override fun run() {
                val currentTime = timeFormat.format(Date())
                binding.tvTime.text = currentTime
                handler.postDelayed(this, 1000)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndFetchPrayerTimes() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = address?.get(0)?.locality ?: "Lokasi Tidak Dikenal"
                binding.tvLocation.text = city

                fetchPrayerTimesFromApi(location.latitude, location.longitude)
            } else {
                Toast.makeText(requireContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPrayerTimesFromApi(lat: Double, lon: Double) {
        val api = RetrofitClient.instance.create(SholatApiService::class.java)

        api.getJadwalSholat(lat, lon).enqueue(object : Callback<SholatResponse> {
            override fun onResponse(call: Call<SholatResponse>, response: Response<SholatResponse>) {
                if (response.isSuccessful) {
                    val timings = response.body()?.data?.timings
                    val jadwal = listOf(
                        JadwalSholat("Subuh", timings?.Fajr ?: "-"),
                        JadwalSholat("Dzuhur", timings?.Dhuhr ?: "-"),
                        JadwalSholat("Ashar", timings?.Asr ?: "-"),
                        JadwalSholat("Maghrib", timings?.Maghrib ?: "-"),
                        JadwalSholat("Isya", timings?.Isha ?: "-")
                    )

                    adapter.submitList(jadwal)
                    viewModel.updateJadwal(jadwal)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data dari API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SholatResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi gagal: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
