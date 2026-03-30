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
import com.troh.todaytask.feature.today.AddTaskBottomSheet
import com.troh.todaytask.feature.today.EditTaskBottomSheet
import com.troh.todaytask.feature.today.TodoEntity
import com.troh.todaytask.feature.today.adapter.TodoAdapter
import com.troh.todaytask.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoAdapter
    private var todoList = mutableListOf<TodoEntity>()
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        initInsets()
        initRecyclerView()
        initBottomNavigation()
        initListeners()
        initTouchHelper()
        loadTodos()
    }

    private fun loadTodos() {
        lifecycleScope.launch {
            val todos = db.todoDao().getAllTodos()
            todoList.clear()
            todoList.addAll(todos)
            todoAdapter.updateList(todos)
        }
    }

    private fun initTouchHelper() {
        val background = ContextCompat.getColor(this, R.color.delete_background).toDrawable()
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete24)

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHold: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) : Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val removedItem = todoList[position]

                lifecycleScope.launch {
                    db.todoDao().delete(removedItem)
                    loadTodos()

                    Toast.makeText(
                        this@MainActivity,
                        "\"${removedItem.title}\" 삭제됨",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dx: Float,
                dy: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val icon = deleteIcon

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // 오른쪽 스와이프
                    if(dx < 0) {
                        background.setBounds(
                            itemView.right + dx.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom)
                        background.draw(c)

                        icon?.let {
                            val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                            val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                            val iconBottom = iconTop + it.intrinsicHeight
                            val iconRight = itemView.right - iconMargin
                            val iconLeft = iconRight - it.intrinsicWidth

                            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            it.draw(c)
                        }
                    } else {
                        background.setBounds(0, 0, 0, 0)
                    }

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dx,
                        dy,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvTodo)
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun initRecyclerView() {
        todoAdapter = TodoAdapter(
            items = todoList,
            onTodoChecked =  { item, isChecked ->
                lifecycleScope.launch {
                    val updatedItem = item.copy(
                        isDone = isChecked,
                        dueDate = if(isChecked) System.currentTimeMillis() else null )
                    db.todoDao().update(updatedItem)
                    loadTodos()
                }
            },
            onTodoTextClicked = { item ->
                EditTaskBottomSheet(item) { updatedTodo ->
                    lifecycleScope.launch {
                        db.todoDao().update(updatedTodo)
                        loadTodos()
                    }
                }.show(supportFragmentManager, "EditTaskBottomSheet")
            }
        )

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
            AddTaskBottomSheet
                .newInstance()
                .show(supportFragmentManager, AddTaskBottomSheet.TAG)
        }

        // 할일추가 클릭시 리스트에 추가
        supportFragmentManager.setFragmentResultListener(
            AddTaskBottomSheet.REQUEST_KEY,
            this
        ) { _, bundle ->
            val text = bundle.getString(AddTaskBottomSheet.BUNDLE_KEY_TASK).orEmpty()
            if(text.isBlank()) return@setFragmentResultListener

            val hasTaskId = bundle.containsKey(AddTaskBottomSheet.BUNDLE_KEY_TASK_ID)
            val taskId = bundle.getLong(AddTaskBottomSheet.BUNDLE_KEY_TASK_ID, -1L)

            lifecycleScope.launch {
                if(hasTaskId && taskId != -1L) {
                    val target = todoList.firstOrNull{it.id == taskId}
                    if (target != null) {
                        db.todoDao().update(target.copy(title = text))
                    }
                } else {
                    val todo = TodoEntity(
                        title = text,
                        isDone = false,
                        scheduledDate = DateUtils.getTodayEndMillis(),
                        dueDate = null
                    )

                    db.todoDao().insert(todo)
                    binding.rvTodo.scrollToPosition(0)
                }
                loadTodos()
            }
        }
    }
}