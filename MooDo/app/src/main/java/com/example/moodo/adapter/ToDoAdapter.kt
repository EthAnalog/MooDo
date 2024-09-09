package com.example.moodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.binding.startToDo.text = todoItem.startDate
        holder.binding.endToDo.text = todoItem.endDate
    }
}