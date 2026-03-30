package com.troh.todaytask.feature.today.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.troh.todaytask.R
import com.troh.todaytask.data.local.AppDatabase
import com.troh.todaytask.databinding.FragmentTodayBinding
import com.troh.todaytask.feature.today.AddTaskBottomSheet
import com.troh.todaytask.feature.today.EditTaskBottomSheet
import com.troh.todaytask.feature.today.TodoEntity
import com.troh.todaytask.feature.today.adapter.TodoAdapter
import com.troh.todaytask.util.DateUtils
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayFragment : Fragment(R.layout.fragment_today) {
    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoAdapter: TodoAdapter
    private val todoList = mutableListOf<TodoEntity>()
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodayBinding.bind(view)

        initViews()
        initRecyclerView()
        initListeners()
        initTouchHelper()
        loadTodos()
    }
    private fun initViews() {
        binding.tvDate.text = DateUtils.getTodayText()
    }
    private fun initRecyclerView() {
        todoAdapter = TodoAdapter(
            items = todoList,
            onTodoChecked = { item, isChecked ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val updatedItem = item.copy(
                        isDone = isChecked,
                        dueDate = if (isChecked) System.currentTimeMillis() else null
                    )
                    db.todoDao().update(updatedItem)
                    loadTodos()
                }
            },
            onTodoTextClicked = { item ->
                EditTaskBottomSheet(item) { updatedTodo ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        db.todoDao().update(updatedTodo)
                        loadTodos()
                    }
                }.show(parentFragmentManager, "EditTaskBottomSheet")
            }
        )

        binding.rvTodo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoAdapter
        }
    }

    private fun initListeners() {
        binding.fabAdd.setOnClickListener {
            AddTaskBottomSheet
                .newInstance()
                .show(parentFragmentManager, AddTaskBottomSheet.TAG)
        }

        parentFragmentManager.setFragmentResultListener(
            AddTaskBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val text = bundle.getString(AddTaskBottomSheet.BUNDLE_KEY_TASK).orEmpty()
            if (text.isBlank()) return@setFragmentResultListener

            viewLifecycleOwner.lifecycleScope.launch {
                val todo = TodoEntity(
                    title = text,
                    isDone = false,
                    scheduledDate = DateUtils.getTodayEndMillis(),
                    dueDate = null
                )
                db.todoDao().insert(todo)
                loadTodos()
                binding.rvTodo.scrollToPosition(0)
            }
        }
    }

    private fun initTouchHelper() {
        val background = ContextCompat.getColor(requireContext(), R.color.delete_background).toDrawable()
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete24)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedItem = todoList[position]

                viewLifecycleOwner.lifecycleScope.launch {
                    db.todoDao().delete(removedItem)
                    loadTodos()

                    Toast.makeText(
                        requireContext(),
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

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dx < 0) {
                        background.setBounds(
                            itemView.right + dx.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
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

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvTodo)
    }

    private fun loadTodos(targetDateMillis: Long = System.currentTimeMillis()) {
        viewLifecycleOwner.lifecycleScope.launch {
            val start = DateUtils.getStartOfDayMillis(targetDateMillis)
            val end = DateUtils.getEndOfDayMillis(targetDateMillis)
            val todos = db.todoDao().getTodosByScheduledDate(start, end)

            todoList.clear()
            todoList.addAll(todos)
            todoAdapter.updateList(todos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}