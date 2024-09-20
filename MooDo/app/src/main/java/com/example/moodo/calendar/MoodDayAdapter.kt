package com.example.moodo.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.R
import com.example.moodo.calendar.DayAdapter.ClickItemDayListener
import com.example.moodo.databinding.ItemListDayBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodDayAdapter(val tempMonth:Int, val dayList:MutableList<Date>,
                     val todayPosition:Int, val userId:String,
                     val dayMdItem:MutableList<String>)
    :RecyclerView.Adapter<MoodDayAdapter.DayHolder>() {
    val row = 5

    // 선택된 날짜
    var selectedPosition = -1
    // 날짜 선택 interface
    interface ClickItemDayListener {
        fun clickItemDay(position: Int)
    }

    var clickItemDayListener: ClickItemDayListener? = null
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

        // 요일 색상 설정(R.color > Color.~로 변경)
        val textColor = when(position%7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        }
        holder.binding.itemDayTxt.setTextColor(textColor)

        // 현재 월이 아닌 날짜 투명하게
        if (tempMonth != currentDay.month) {
            holder.binding.itemDayTxt.alpha = 0.4f
        }
        else {
            holder.binding.itemDayTxt.alpha = 1.0f
        }

        val todayMd = dayMdItem[position]

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

        if (selectedPosition== -1 && todayPosition == position) {
            selectedPosition = todayPosition
            clickItemDayListener?.clickItemDay(selectedPosition)
        }

        // 선택된 항목 배경색 설정
        if (selectedPosition == position) {
            holder.binding.itemDayTxt.setBackgroundResource(R.drawable.select_day)
            holder.binding.itemDayTxt.setTextColor(Color.WHITE)
        } else {
            holder.binding.itemDayTxt.setBackgroundResource(R.drawable.none_select_day)
            holder.binding.itemDayTxt.setTextColor(Color.BLACK)
        }
    }
}