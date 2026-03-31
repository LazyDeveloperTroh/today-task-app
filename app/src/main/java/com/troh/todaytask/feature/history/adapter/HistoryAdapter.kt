package com.troh.todaytask.feature.history

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.troh.todaytask.R
import com.troh.todaytask.databinding.ItemHistoryHeaderBinding
import com.troh.todaytask.databinding.ItemHistoryTodoBinding
import com.troh.todaytask.util.DateUtils

class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HistoryUiModel>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun submitList(newItems: List<HistoryUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryUiModel.Header -> TYPE_HEADER
            is HistoryUiModel.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHistoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }

            else -> {
                val binding = ItemHistoryTodoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryUiModel.Header -> (holder as HeaderViewHolder).bind(item)
            is HistoryUiModel.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(
        private val binding: ItemHistoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryUiModel.Header) {
            binding.tvDate.text = item.title
            binding.tvCount.text = "${item.count} items"
        }
    }

    class ItemViewHolder(
        private val binding: ItemHistoryTodoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryUiModel.Item) {
            val todo = item.todo

            binding.tvTitle.text = todo.title

            if (todo.isDone) {
                binding.ivStatus.setImageResource(R.drawable.check_circle_24)
                binding.ivStatus.setColorFilter((Color.parseColor("#60A5FA")))
                binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTitle.setTextColor(Color.parseColor("#9CA3AF"))
            } else {
                binding.ivStatus.setImageResource(R.drawable.cancel_24)
                binding.ivStatus.setColorFilter((Color.parseColor("#F4B4B4")))
                binding.tvTitle.paintFlags = binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTitle.setTextColor(Color.parseColor("#111827"))
            }
        }
    }
}