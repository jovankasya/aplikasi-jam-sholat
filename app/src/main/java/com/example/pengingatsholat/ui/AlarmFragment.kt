package com.example.pengingatsholat.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pengingatsholat.adapter.AlarmAdapter
import com.example.pengingatsholat.databinding.FragmentAlarmBinding
import com.example.pengingatsholat.model.AlarmSholat
import com.example.pengingatsholat.service.AlarmReceiver
import com.example.pengingatsholat.viewmodel.SholatViewModel
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var adapter: AlarmAdapter
    private val alarmList = mutableListOf<AlarmSholat>()
    private lateinit var viewModel: SholatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[SholatViewModel::class.java]

        adapter = AlarmAdapter(requireContext(), alarmList) { alarm, isChecked ->
            alarm.aktif = isChecked
            if (isChecked) {
                setAlarm(alarm)
            } else {
                cancelAlarm(alarm)
            }
        }

        binding.rvAlarmSholat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlarmSholat.adapter = adapter

        viewModel.jadwalSholat.observe(viewLifecycleOwner) { list ->
            alarmList.clear()
            list.forEach {
                alarmList.add(AlarmSholat(it.nama, it.waktu))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setAlarm(alarm: AlarmSholat) {
        try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = timeFormat.parse(alarm.waktu) ?: return
            val cal = Calendar.getInstance().apply {
                time = date
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // Pastikan waktu target lebih besar dari sekarang
                val now = Calendar.getInstance()
                set(Calendar.YEAR, now.get(Calendar.YEAR))
                set(Calendar.MONTH, now.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                if (before(now)) {
                    add(Calendar.DAY_OF_MONTH, 1) // jadwal sudah lewat, set untuk besok
                }
            }

            val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                putExtra("nama_sholat", alarm.nama)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                alarm.nama.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )

            Toast.makeText(requireContext(), "${alarm.nama} diaktifkan", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal set alarm: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelAlarm(alarm: AlarmSholat) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.nama.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Toast.makeText(requireContext(), "${alarm.nama} dinonaktifkan", Toast.LENGTH_SHORT).show()
    }
}
