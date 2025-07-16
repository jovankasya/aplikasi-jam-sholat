package com.example.pengingatsholat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pengingatsholat.databinding.ItemSholatBinding
import com.example.pengingatsholat.model.JadwalSholat

class SholatAdapter : RecyclerView.Adapter<SholatAdapter.SholatViewHolder>() {

    private val list = ArrayList<JadwalSholat>()

    fun submitList(data: List<JadwalSholat>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class SholatViewHolder(private val binding: ItemSholatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JadwalSholat) {
            binding.tvNamaSholat.text = data.nama
            binding.tvWaktuSholat.text = data.waktu
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SholatViewHolder {
        val binding = ItemSholatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SholatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SholatViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
