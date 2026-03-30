package com.troh.todaytask

import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.troh.todaytask.data.local.AppDatabase
import com.troh.todaytask.databinding.ActivityMainBinding
import com.troh.todaytask.feature.history.fragment.HistoryFragment
import com.troh.todaytask.feature.today.AddTaskBottomSheet
import com.troh.todaytask.feature.today.EditTaskBottomSheet
import com.troh.todaytask.feature.today.TodoEntity
import com.troh.todaytask.feature.today.adapter.TodoAdapter
import com.troh.todaytask.feature.today.fragment.TodayFragment
import com.troh.todaytask.util.DateUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        initInsets()
        initBottomNavigation()

        if(savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.menu_todo
        }
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_todo -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, TodayFragment())
                        .commit()
                    true
                }

                R.id.menu_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HistoryFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.menu_todo

    }
}