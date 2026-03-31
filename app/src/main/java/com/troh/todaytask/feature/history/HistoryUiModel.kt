package com.troh.todaytask.feature.history

import com.troh.todaytask.feature.today.TodoEntity

sealed class HistoryUiModel {
    data class Header(
        val title: String,
        val count: Int
    ) : HistoryUiModel()

    data class Item(
        val todo: TodoEntity
    ) : HistoryUiModel()
}