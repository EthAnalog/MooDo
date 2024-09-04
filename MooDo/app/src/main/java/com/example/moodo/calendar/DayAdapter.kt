package com.example.moodo.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.databinding.ItemListDayBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayAdapter(val tempMonth:Int,
                 val dayList:MutableList<Date>,
                 val today:Date)
    :RecyclerView.Adapter<DayAdapter.DayHolder>() {
    val row = 6

    var selectedDate:Date? = null

    // 날짜 선택 interface
    interface ClickItemDayListener {
        fun clickItemDay(date:Date)
    }

    var clickItemDayListener:ClickItemDayListener? = null
    inner class DayHolder(val binding: ItemListDayBinding) :RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemDayLayout.setOnClickListener {
                // 날짜 클릭
                val pos = adapterPosition
                Toast.makeText(binding.root.context, "${dayList[pos]}", Toast.LENGTH_SHORT).show()

                if (pos != RecyclerView.NO_POSITION) {
                    // 클릭된 날짜 색상 변경
                    selectedDate = dayList[pos]
                    notifyDataSetChanged()

                    clickItemDayListener?.clickItemDay(dayList[pos])

                    Log.d("MooDoLog click", dayList[pos].toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        return DayHolder(ItemListDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return row*7
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        val currentDay = dayList[position]

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayString = sdf.format(today)
        val currentDayString = sdf.format(currentDay)
        val selectedDateString = selectedDate?.let { sdf.format(it) } ?: ""

        holder.binding.itemDayTxt.text = currentDay.date.toString()

        // 요일 색상 설정
        holder.binding.itemDayTxt.setTextColor(when(position%7){
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        if (tempMonth != currentDay.month) {
            holder.binding.itemDayTxt.alpha = 0.4f
        }

        // 선택된 날짜 및 현재 날짜 배경색 설정
        if (selectedDate != null) {
            holder.binding.itemDayLayout.setBackgroundColor(
                when {
                    currentDayString == selectedDateString -> Color.parseColor("#FFC107") // 선택된 날짜 강조 색상
                    else -> Color.WHITE // 기본 배경색
                }
            )
        }
        else {
            holder.binding.itemDayLayout.setBackgroundColor(
                when {
                    currentDayString == todayString -> Color.parseColor("#FFEB3B") // 오늘 날짜 강조 색상
                    else -> Color.WHITE // 기본 배경색
                }
            )
        }
    }
}