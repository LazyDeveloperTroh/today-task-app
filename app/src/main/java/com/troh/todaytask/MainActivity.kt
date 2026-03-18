package com.troh.todaytask

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.troh.todaytask.databinding.ActivityMainBinding
import com.troh.todaytask.feature.today.AddTaskBottomSheet
import com.troh.todaytask.feature.today.TodoItem
import com.troh.todaytask.feature.today.adapter.TodoAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoAdapter
    private var todoList = mutableListOf<TodoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        initInsets()
        initRecyclerView()
        initBottomNavigation()
        initListeners()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun initRecyclerView() {
        todoList = mutableListOf(
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
        // 할일 추가 팝업 버튼
        binding.fabAdd.setOnClickListener {
            AddTaskBottomSheet().show(supportFragmentManager, AddTaskBottomSheet.TAG)
        }

        // 할일추가 클릭시 리스트에 추가
        supportFragmentManager.setFragmentResultListener(
            AddTaskBottomSheet.REQUEST_KEY,
            this
        ) { _, bundle ->
            val text = bundle.getString(AddTaskBottomSheet.BUNDLE_KEY_TASK).orEmpty()
            if (text.isNotBlank()) {
                todoList.add(0, TodoItem(text))
                todoAdapter.notifyItemInserted(0)
                binding.rvTodo.scrollToPosition(0)
            }
        }
    }

    private fun showAddTodoDialog() {
        val editText = EditText(this).apply {
            hint = "할 일을 입력하세요"
            setSingleLine(true)
            setPadding(50, 40, 50, 40)
        }

        AlertDialog.Builder(this)
            .setTitle("할 일 추가")
            .setView(editText)
            .setPositiveButton("추가") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    todoList.add(0, TodoItem(text))
                    todoAdapter.notifyItemInserted(0)
                    binding.rvTodo.scrollToPosition(0)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}