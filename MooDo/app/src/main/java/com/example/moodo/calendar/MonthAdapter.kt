package com.example.moodo.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.R
import com.example.moodo.calendar.DayAdapter.ClickItemDayListener
import com.example.moodo.databinding.ItemListMonthBinding
import java.util.Calendar
import java.util.Date

class MonthAdapter()
    :RecyclerView.Adapter<MonthAdapter.MonthHolder>() {
    val center = Int.MAX_VALUE/2
    private var calendar = Calendar.getInstance()
    val today = calendar.time // 오늘 날짜

    var dayAdapter:DayAdapter? = null
    var clickItemDayListener: DayAdapter.ClickItemDayListener? = null

    inner class MonthHolder(val binding: ItemListMonthBinding) :RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthHolder {
        return MonthHolder(ItemListMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: MonthHolder, position: Int) {
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, position-center)

        holder.binding.itemMonthTxt.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        val tempMonth = calendar.get(Calendar.MONTH)

        val dayList:MutableList<Date> = MutableList(6*7){Date()}
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        // adapter 생성 및 리스너 설정
        dayAdapter = DayAdapter(tempMonth, dayList, today)

        holder.binding.itemMonthDayList.layoutManager = GridLayoutManager(holder.binding.root.context, 7)
        holder.binding.itemMonthDayList.adapter = dayAdapter

    }
}