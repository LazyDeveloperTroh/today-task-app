package com.troh.todaytask

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.troh.todaytask.databinding.ActivityMainBinding
import com.troh.todaytask.feature.today.TodoItem
import com.troh.todaytask.feature.today.adapter.TodoAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        initRecyclerView()
        initBottomNavigation()
        initListeners()
    }

    private fun initRecyclerView() {
        val todoList = mutableListOf(
            TodoItem("프로젝트 제안서 검토"),
            TodoItem("식료품 사기"),
            TodoItem("은행 전화하기"),
            TodoItem("아침 요가 세션", true),
            TodoItem("회의 발표 자료 준비"),
            TodoItem("프로젝트 제안서 검토"),
            TodoItem("식료품 사기"),
            TodoItem("은행 전화하기"),
            TodoItem("아침 요가 세션", true),
            TodoItem("회의 발표 자료 준비"),
            TodoItem("식료품 사기"),
            TodoItem("은행 전화하기"),
            TodoItem("아침 요가 세션", true),
            TodoItem("회의 발표 자료 준비")
        )

        todoAdapter = TodoAdapter(todoList)

        binding.rvTodo.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.menu_todo

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_todo -> {
                    Toast.makeText(this, "할일 화면", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_project -> {
                    Toast.makeText(this, "프로젝트 화면", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_calendar -> {
                    Toast.makeText(this, "달력 화면", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun initListeners() {
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "할 일 추가", Toast.LENGTH_SHORT).show()
        }
    }
}