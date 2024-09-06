package com.example.moodo.fragment

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.moodo.MainActivity_ToDo
import com.example.moodo.R
import com.example.moodo.calendar.MonthAdapter
import com.example.moodo.databinding.FragmentHomeBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.recycler.ToDoAdapter
import retrofit2.Call
import retrofit2.Response
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding:FragmentHomeBinding

    private var userId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // adapter
        val todoAdapter = ToDoAdapter()

        // recyclerView 연결
        binding.todoListLayout.adapter = todoAdapter
        binding.todoListLayout.layoutManager = LinearLayoutManager(requireContext())

        // custom calendar
        val monthListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = MonthAdapter().apply {
            // 날짜 선택 처리
            onDaySelectedListener = object :MonthAdapter.OnDaySelectedListener{
                override fun onDaySelected(date: String) {
                    Log.d("MooDoLog Id", userId.toString())
                    Log.d("MooDoLog day", date)

                    MooDoClient.retrofit.getTodoList(userId.toString(), date).enqueue(object :retrofit2.Callback<List<MooDoToDo>>{
                        override fun onResponse(
                            call: Call<List<MooDoToDo>>,
                            response: Response<List<MooDoToDo>>
                        ) {
                            if (response.isSuccessful) {
                                val todoList = response.body() ?: mutableListOf()

                                todoAdapter.todoList.clear()
                                todoAdapter.todoList.addAll(todoList)
                                todoAdapter.notifyDataSetChanged()
                            }else {
                                Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                            Log.d("MooDoLog getTodo Fail", t.toString())
                        }
                    })
                    binding.selectTxt.text = date
                }
            }
        }
        binding.calendarCustom.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarCustom)

        // 작성 수정 및 무드 트래커 이동
        binding.userMooDo.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity_ToDo::class.java)
            val selectDate = binding.selectTxt.text.toString()

            intent.putExtra("userId", userId.toString())
            intent.putExtra("selectDate", selectDate)

            startActivity(intent)
        }
    }
    companion object {
        // Fragment 생성 시 userId 전달하는 newInstance 메서드
        @JvmStatic
        fun newInstance(userId:String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", userId)
                }
            }
    }
}