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
                 val todayPosition:Int)
    :RecyclerView.Adapter<DayAdapter.DayHolder>() {
    val row = 6

    // 선택된 날짜
    var selectedPosition = -1
    // 날짜 선택 interface
    interface ClickItemDayListener {
        fun clickItemDay(position: Int)
    }

    var clickItemDayListener:ClickItemDayListener? = null
    inner class DayHolder(val binding: ItemListDayBinding) :RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemDayLayout.setOnClickListener {
                // 날짜 클릭
                val pos = adapterPosition
                Toast.makeText(binding.root.context, "${dayList[pos]}", Toast.LENGTH_SHORT).show()

                clickItemDayListener?.clickItemDay(pos)
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

        if (selectedPosition== -1 && todayPosition == position) {
            selectedPosition = todayPosition
            clickItemDayListener?.clickItemDay(selectedPosition)
        }
    }
}