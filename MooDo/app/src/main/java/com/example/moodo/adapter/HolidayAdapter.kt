package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.databinding.ItemHolidayBinding
import com.example.moodo.db.MooDoHoliday

class HolidayAdapter():RecyclerView.Adapter<HolidayAdapter.Holder>() {
    var holidayList = mutableListOf<MooDoHoliday>()
    class Holder(val binding:ItemHolidayBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemHolidayBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return holidayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.itemHolidayName.text = holidayList[position].dateName
    }

}