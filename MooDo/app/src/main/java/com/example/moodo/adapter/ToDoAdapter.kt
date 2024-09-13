package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodo.R
import com.example.moodo.databinding.ItemTodoListBinding
import com.example.moodo.db.MooDoToDo
import java.text.SimpleDateFormat
import java.util.Locale

class ToDoAdapter() :RecyclerView.Adapter<ToDoAdapter.ToDoHolder>() {
    var todoList = mutableListOf<MooDoToDo>()

    interface OnItemClickLister {
        fun onItemClick(pos:Int)
    }
    var onItemClickLister:OnItemClickLister? = null

    inner class ToDoHolder(val binding:ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClickLister?.onItemClick(adapterPosition)
            }
        }
    }

    // 추가
    fun addItem(todoItem:MooDoToDo) {
        todoList.add(todoItem)
        notifyDataSetChanged()
    }

    // 수정
    fun updateItem(pos: Int, toDo: MooDoToDo) {
        todoList.set(pos, toDo)
        notifyDataSetChanged()
    }
    // 삭제
    fun removeItem(pos:Int) {
        todoList.removeAt(pos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoHolder {
        return ToDoHolder(ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: ToDoHolder, position: Int) {
        val todoItem = todoList[position]

        holder.binding.itemToDo.text = todoItem.tdList

        // 문자열 포맷 이전 시작 시간, 종료 시간 저장하는 숨겨진 textView
        holder.binding.saveStartDate.text = todoItem.startDate
        holder.binding.saveEndDate.text = todoItem.endDate

        // 시간 포맷팅
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // 출력 형식
        val outputFormat = SimpleDateFormat("M/d a hh:mm", Locale.getDefault())

        // startDate
        val startDate = inputFormat.parse(todoItem.startDate)
        val formattedStartDate = startDate?.let { outputFormat.format(it) } ?: ""

        // endDate
        val endDate = inputFormat.parse(todoItem.endDate)
        val formattedEndDate = endDate?.let { outputFormat.format(it) } ?: ""

        holder.binding.startToDo.text = formattedStartDate
        holder.binding.endToDo.text = formattedEndDate

        // 색깔에 따른 box
        when(todoItem.color) {
            "red" -> holder.binding.tdListBox.setBackgroundResource(R.drawable.td_red_box)
            "blue" -> holder.binding.tdListBox.setBackgroundResource(R.drawable.td_blue_box)
            "orange" -> holder.binding.tdListBox.setBackgroundResource(R.drawable.td_orange_box)
            "green" -> holder.binding.tdListBox.setBackgroundResource(R.drawable.td_green_box)
            "yellow" -> holder.binding.tdListBox.setBackgroundResource(R.drawable.td_yellow_box)
        }

        when(todoItem.tdCheck) {
            "N" -> holder.binding.tdChecked.setImageResource(R.drawable.td_list_non_check)
            "Y" -> holder.binding.tdChecked.setImageResource(R.drawable.td_list_check_black)
        }
    }
}