package com.example.moodo.calendar

import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.R
import com.example.moodo.databinding.ItemListDayBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoUser
import com.example.moodo.db.MoodoCalendar
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Optional

class DayAdapter(val tempMonth:Int,
                 val dayList:MutableList<Date>,
                 val todayPosition:Int,
                 val userId:String,
                 val dayCalendar:MutableList<MoodoCalendar>)
    :RecyclerView.Adapter<DayAdapter.DayHolder>() {
    val row = 5

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
                // 현재 월인지 확인
                if (tempMonth == dayList[adapterPosition].month) {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition

                    // 이전 선택 항목과 현재 선택 항목을 업데이트
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    clickItemDayListener?.clickItemDay(selectedPosition)
                }
//                val previousPosition = selectedPosition
//                selectedPosition = adapterPosition
//
//                // 이전 선택 항목과 현재 선택 항목을 업데이트
//                notifyItemChanged(previousPosition)
//                notifyItemChanged(selectedPosition)
//
//                clickItemDayListener?.clickItemDay(selectedPosition)
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDay)

        holder.binding.itemDayTxt.text = currentDay.date.toString()

        // 요일 색상 설정
        val textColor = when(position % 7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        }
        holder.binding.itemDayTxt.setTextColor(textColor)

        // 현재 월이 아닌 날짜 투명하게
        holder.binding.itemDayTxt.alpha = if (tempMonth != currentDay.month) 0.4f else 1.0f

        val todayTd = dayCalendar[position].todayTd
        val todayMd = dayCalendar[position].todayMd
        val todayHoliday = dayCalendar[position].isHoliday

        // 공휴일 처리
        if (todayHoliday == "Y") {
            holder.binding.itemDayTxt.setTextColor(Color.RED)
        } else {
            holder.binding.itemDayTxt.setTextColor(Color.BLACK)
        }

        when(todayTd) {
            0 -> holder.binding.todoOval.setImageResource(0)
            else -> holder.binding.todoOval.setImageResource(R.drawable.td_has)
        }

        // 감정 아이콘 설정
        when(todayMd) {
            "b_1" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_angry)
            "b_2" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_sad)
            "b_3" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_meh)
            "b_4" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_s_happy)
            "b_5" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_happy)
            "b_0" -> holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
            "b_" -> holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
            "1" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_angry)
            "2" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_sad)
            "3" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_meh)
            "4" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_s_happy)
            "5" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_happy)
            else -> holder.binding.itemMood.setImageResource(0)
        }

        // 오늘 날짜와 선택된 날짜 처리
        if (selectedPosition == -1 && todayPosition == position) {
            selectedPosition = todayPosition
            clickItemDayListener?.clickItemDay(selectedPosition)
        }

        // 선택된 항목 배경색 설정
        if (selectedPosition == position) {
            holder.binding.itemDayTxt.setBackgroundResource(R.drawable.select_day)
        } else {
            holder.binding.itemDayTxt.setBackgroundResource(R.drawable.none_select_day)
        }
    }

}