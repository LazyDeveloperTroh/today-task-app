package com.troh.todaytask.feature.history

import com.troh.todaytask.feature.today.TodoEntity
import com.troh.todaytask.util.DateUtils

object HistoryUiMapper {

    fun map(todos: List<TodoEntity>): List<HistoryUiModel> {
        val grouped = todos.groupBy { todo ->
            DateUtils.formatDate(todo.scheduledDate ?: 0L)
        }

        val result = mutableListOf<HistoryUiModel>()

        grouped.forEach { (dateText, items) ->
            result.add(
                HistoryUiModel.Header(
                    title = DateUtils.formatHistoryHeader(items.first().scheduledDate ?: 0L),
                    count = items.size
                )
            )
            items.forEach { todo ->
                result.add(HistoryUiModel.Item(todo))
            }
        }

        return result
    }
}