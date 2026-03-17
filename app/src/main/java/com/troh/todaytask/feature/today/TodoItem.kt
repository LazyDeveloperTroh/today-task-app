package com.troh.todaytask.feature.today

data class TodoItem (
    val title: String,
    var isDone: Boolean = false
)