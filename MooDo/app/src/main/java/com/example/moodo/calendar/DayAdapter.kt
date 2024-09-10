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
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Optional

class DayAdapter(val tempMonth:Int,
                 val dayList:MutableList<Date>,
                 val todayPosition:Int,
                 val userId:String)
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

        // 요일 색상 설정(R.color > Color.~로 변경)
        val textColor = when(position%7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        }
        holder.binding.itemDayTxt.setTextColor(textColor)

        if (tempMonth != currentDay.month) {
            holder.binding.itemDayTxt.alpha = 0.4f
        }
        else {
            holder.binding.itemDayTxt.alpha = 1.0f
        }

        // 기분 데이터에 따라 이미지 변경
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDay)
        MooDoClient.retrofit.getMdMode(userId, formattedDate).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    if (response.body() != null) {
                        when(response.body()) {
                            1 -> holder.binding.itemMood.setImageResource(R.drawable.angry)
                            2 -> holder.binding.itemMood.setImageResource(R.drawable.sad)
                            3 -> holder.binding.itemMood.setImageResource(R.drawable.meh)
                            4 -> holder.binding.itemMood.setImageResource(R.drawable.s_happy)
                            5 -> holder.binding.itemMood.setImageResource(R.drawable.happy)
                            else -> holder.binding.itemMood.setImageResource(R.drawable.no_mood)
                        }
                    }
                    else {
                        holder.binding.itemMood.setImageResource(R.drawable.no_mood)
                    }
                }
                else{
                    holder.binding.itemMood.setImageResource(R.drawable.no_mood)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog modFail", t.toString())
            }
        })
        MooDoClient.retrofit.getTodoCountForDay(userId, formattedDate).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    if (response.body() != null) {
                        when(response.body()) {
                            0 -> holder.binding.todoOval.setImageResource(R.drawable.td_none)
                            else -> holder.binding.todoOval.setImageResource(R.drawable.td_has)
                        }
                    }
                    else {
                        holder.binding.todoOval.setImageResource(R.drawable.td_none)
                    }
                }
                else{
                    holder.binding.todoOval.setImageResource(R.drawable.td_none)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog emjFail", t.toString())
            }
        })

        if (selectedPosition== -1 && todayPosition == position) {
            selectedPosition = todayPosition
            clickItemDayListener?.clickItemDay(selectedPosition)
        }
    }
}