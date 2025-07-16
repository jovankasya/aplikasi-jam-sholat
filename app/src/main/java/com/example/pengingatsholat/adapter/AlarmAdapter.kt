package com.example.pengingatsholat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pengingatsholat.databinding.ItemAlarmBinding
import com.example.pengingatsholat.model.AlarmSholat

class AlarmAdapter(
    private val context: Context,
    private val alarmList: List<AlarmSholat>,
    private val onSwitchChanged: (AlarmSholat, Boolean) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: AlarmSholat) {
            binding.tvNamaSholat.text = alarm.nama
            binding.tvWaktuSholat.text = alarm.waktu
            binding.switchAlarm.isChecked = alarm.aktif

            binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
                onSwitchChanged(alarm, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = alarmList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }
}
