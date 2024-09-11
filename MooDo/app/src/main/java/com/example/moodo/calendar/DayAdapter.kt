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
                 val userId:String,
                 val userAge:String)
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

    Log.d("MooDoDate", formattedDate)

    val birthDayFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val userBirthday = birthDayFormat.format(SimpleDateFormat("yyyy/MM/dd").parse(userAge)) // 생일을 MM-dd 형식으로 변환
    val formattedBirth = birthDayFormat.format(currentDay)
    Log.d("MooDoLog UserInfo", userBirthday)

    // 생일인 경우 날짜에 표기
    if (userBirthday == formattedBirth) {
      Log.d("MooDoLog UserInfo", userBirthday)
      updateTodo(holder, userId, formattedDate)
      MooDoClient.retrofit.getMdMode(userId, formattedDate).enqueue(object : retrofit2.Callback<Int> {
        override fun onResponse(call: Call<Int>, response: Response<Int>) {
          if (response.isSuccessful) {
            when (response.body()) {
              1 -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_angry)
              2 -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_sad)
              3 -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_meh)
              4 -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_s_happy)
              5 -> holder.binding.itemMood.setImageResource(R.drawable.ic_birthday_happy)
              else -> holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
            }
          } else {
            holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
          }
        }
        override fun onFailure(call: Call<Int>, t: Throwable) {
          holder.binding.itemMood.setImageResource(R.drawable.user_birthday_non_emoji)
        }
      })
    } else {
      updateMood(holder, userId, formattedDate)
      updateTodo(holder, userId, formattedDate)
    }

    //updateMood(holder, userId, formattedDate)
    //updateTodo(holder, userId, formattedDate)

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
  // 기분 및 할 일 데이터
  private fun updateTodo(holder: DayHolder, userId: String, formattedDate: String) {
    MooDoClient.retrofit.getTodoCountForDay(userId, formattedDate).enqueue(object : retrofit2.Callback<Int> {
      override fun onResponse(call: Call<Int>, response: Response<Int>) {
        if (response.isSuccessful) {
          when (response.body()) {
            0 -> holder.binding.todoOval.setImageResource(R.drawable.td_none)
            else -> holder.binding.todoOval.setImageResource(R.drawable.td_has)
          }
        } else {
          holder.binding.todoOval.setImageResource(R.drawable.td_none)
        }
      }

      override fun onFailure(call: Call<Int>, t: Throwable) {
        Log.d("MooDoLog emjFail", t.toString())
      }
    })
  }
  private fun updateMood(holder:DayHolder, userId:String, formattedDate: String) {
    MooDoClient.retrofit.getMdMode(userId, formattedDate).enqueue(object : retrofit2.Callback<Int> {
      override fun onResponse(call: Call<Int>, response: Response<Int>) {
        if (response.isSuccessful) {
          when (response.body()) {
            1 -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_angry)
            2 -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_sad)
            3 -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_meh)
            4 -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_s_happy)
            5 -> holder.binding.itemMood.setImageResource(R.drawable.ic_emotion_happy)
            else -> holder.binding.itemMood.setImageResource(R.drawable.no_mood)
          }
        } else {
          holder.binding.itemMood.setImageResource(R.drawable.no_mood)
        }
      }

      override fun onFailure(call: Call<Int>, t: Throwable) {
        Log.d("MooDoLog modFail", t.toString())
      }
    })
  }
}