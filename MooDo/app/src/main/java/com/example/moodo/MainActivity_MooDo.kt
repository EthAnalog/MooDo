package com.example.moodo

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.moodo.databinding.ActivityMainMooDoBinding
import com.example.moodo.fragment.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity_MooDo : AppCompatActivity() {
    lateinit var binding:ActivityMainMooDoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMooDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 사용자 id
        val userId = intent.getStringExtra("id").toString()

        // 초기 화면 설정
        loadFragment(HomeFragment.newInstance(userId))

        // bottomNav
        binding.bottomAppBar.setOnNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.homeBtn -> loadFragment(HomeFragment.newInstance(userId))
            }
            true
        }
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}