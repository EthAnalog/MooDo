package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.databinding.ItemMoodBinding
import com.example.moodo.db.MooDoMode

class MoodAdapter() : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    val moodList = mutableListOf<MooDoMode>()

    class MoodViewHolder(val binding:ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        return MoodViewHolder(ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val modeItem = moodList[position]

        holder.binding.tvMoodValue.text = modeItem.mdMode.toString()
        holder.binding.tvDate.text = modeItem.createdDate
    }
}