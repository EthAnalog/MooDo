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
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Optional

class DayAdapter(val tempMonth:Int,
                 val dayList:MutableList<Date>,
                 val todayPosition:Int,
                 val emojiList:MutableList<String>,
                 val tdCntList:MutableList<Int>,
                 val userId:String)
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
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                // 이전 선택 항목과 현재 선택 항목을 업데이트
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                clickItemDayListener?.clickItemDay(selectedPosition)
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

        // Log.d("MooDoDate", formattedDate)

        val emoji = emojiList[position]
        Log.d("MooDoLog Emoji", emoji)
        when(emoji) {
            "birthday_angry" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_angry)
            "birthday_sad" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_sad)
            "birthday_meh" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_meh)
            "birthday_s_happy" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_s_happy)
            "birthday_happy" -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_happy)
            "birthday_none" -> holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
            "angry" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_angry)
            "sad" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_sad)
            "meh" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_meh)
            "s_happy" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_s_happy)
            "happy" -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_happy)
        }

        val tdCnt = tdCntList[position]
        Log.d("MooDoLog tdCnt", emoji)
        if (tdCnt > 0) {
            holder.binding.todoOval.setImageResource(R.drawable.td_has)
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