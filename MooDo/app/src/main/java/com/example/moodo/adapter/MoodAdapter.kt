package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.R
import com.example.moodo.databinding.ItemStatisBinding
import com.example.moodo.db.MooDoMode
import java.text.SimpleDateFormat
import java.util.Locale

class MoodAdapter() : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    val moodList = mutableListOf<MooDoMode>()

    interface OnItemClickLister {
        fun onItemClick(pos:Int)
        fun onItemLongClick(pos:Int)
    }
    var onItemClickLister:OnItemClickLister? = null
    inner class MoodViewHolder(val binding:ItemStatisBinding) : RecyclerView.ViewHolder(binding.root) {
        init  {
            itemView.setOnClickListener {
                onItemClickLister?.onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                onItemClickLister?.onItemLongClick(adapterPosition)
                true
            }
        }
    }

    // 삭제
    fun removeItem(pos:Int) {
        moodList.removeAt(pos)
        notifyDataSetChanged()
    }
    // 수정
    fun updateItem(pos:Int, mode: MooDoMode) {
        moodList.set(pos, mode)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        return MoodViewHolder(ItemStatisBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val modeItem = moodList[position]

        when(modeItem.mdMode) {
            1 -> holder.binding.moodIcon.setImageResource(R.drawable.ic_emotion_angry)
            2 -> holder.binding.moodIcon.setImageResource(R.drawable.ic_emotion_sad)
            3 -> holder.binding.moodIcon.setImageResource(R.drawable.ic_emotion_meh)
            4 -> holder.binding.moodIcon.setImageResource(R.drawable.ic_emotion_s_happy)
            5 -> holder.binding.moodIcon.setImageResource(R.drawable.ic_emotion_happy)
            else -> holder.binding.moodIcon.setImageResource(R.drawable.no_mood)
        }

        when(modeItem.weather) {
            1 -> holder.binding.weatherIcon.setImageResource(R.drawable.ic_weather_sun)
            2 -> holder.binding.weatherIcon.setImageResource(R.drawable.ic_weather_cloudy)
            3 -> holder.binding.weatherIcon.setImageResource(R.drawable.ic_weather_rain)
            4 -> holder.binding.weatherIcon.setImageResource(R.drawable.ic_weather_snow)
            else -> holder.binding.moodIcon.setImageResource(R.drawable.no_mood)
        }

        // 포맷팅 전 시간 저장
        holder.binding.saveDescription.text = modeItem.createdDate

        // 시간 포맷팅
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // 출력 형식
        val outputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

        val formatter = inputFormat.parse(modeItem.createdDate)
        val formattedDate = formatter?.let { outputFormat.format(it) } ?: ""
        holder.binding.moodDescription.text = formattedDate

        holder.binding.oneLineDiary.text = modeItem.mdDaily
    }
}