package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.databinding.ItemHolidayBinding
import com.example.moodo.db.MooDoHoliday
import java.text.SimpleDateFormat
import java.util.Locale

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
        val hdItem = holidayList[position]

        holder.binding.itemHolidayName.text = hdItem.dateName

        // 시간 포맷팅
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("M/d", Locale.getDefault())

        // startDate
        val startDate = inputFormat.parse(hdItem.locdate)
        val formattedStartDate = startDate?.let { outputFormat.format(it) } ?: ""

        // endDate
        val endDate = inputFormat.parse(hdItem.locdate)
        val formattedEndDate = endDate?.let { outputFormat.format(it) } ?: ""

        holder.binding.startToDo.text = "${formattedStartDate} AM 00:00"
        holder.binding.endToDo.text = "${formattedEndDate} PM 11:59"
    }

}