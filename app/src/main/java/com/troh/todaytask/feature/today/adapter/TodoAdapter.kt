package com.troh.todaytask.feature.today.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.troh.todaytask.databinding.ItemTodoBinding
import com.troh.todaytask.feature.today.TodoEntity

/**
 * RecyclerView에 들어갈 데이터를 받아 (item_todo.xml)에 연결하여 데이터를 노출하고,
 * 체크박스 클릭 같은 데이터 이벤트를 처리함
 */
class TodoAdapter (
    private val items: MutableList<TodoEntity>,
    private val onTodoChecked: (TodoEntity, Boolean) -> Unit,
    private val onTodoTextClicked: (TodoEntity) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    /**
     * ViewHolder - 아이템을 매번 생성하지 않고 재사용함
     */
    inner class TodoViewHolder(
        private val binding: ItemTodoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * ViewHolder에 데이터를 연결함
         */
        fun bind(item: TodoEntity) {
            binding.tvTodoTitle.text = item.title
            binding.checkTodo.isChecked = item.isDone
            updateTextStyle(item.isDone)

            binding.checkTodo.setOnClickListener {
                val isChecked = binding.checkTodo.isChecked
                updateTextStyle(item.isDone)
                onTodoChecked(item, isChecked)
            }

            binding.tvTodoTitle.setOnClickListener {
                onTodoTextClicked(item)
            }
        }

        private fun updateTextStyle(isDone: Boolean) {
            if(isDone) {
                binding.tvTodoTitle.paintFlags =
                    binding.tvTodoTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTodoTitle.alpha = 0.45f
            } else {
                binding.tvTodoTitle.paintFlags =
                    binding.tvTodoTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTodoTitle.alpha = 1f
            }
        }
    }

    /**
     * 리스트에 아이템 뷰를 생성할 때 호출됨(대충 화면에 보이는 아이템 수만큼?)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    /**
     * 생성된 ViewHolder에 실제 데이터를 연결함
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<TodoEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}