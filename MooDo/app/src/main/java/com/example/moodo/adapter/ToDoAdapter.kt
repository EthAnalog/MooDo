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

    class ToDoHolder(val binding:ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoHolder {
        return ToDoHolder(ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: ToDoHolder, position: Int) {
        val todoItem = todoList[position]
        holder.binding.itemToDo.text = todoItem.tdList

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // startDate 포맷팅
        holder.binding.startToDo.text = dateFormat.format(todoItem.startDate)
        // endDate 포맷팅
        holder.binding.endToDo.text = dateFormat.format(todoItem.endDate)
    }
}