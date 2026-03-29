package com.troh.todaytask.feature.today

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isDone: Boolean = false,
    val scheduledDate: Long? = null,
    val dueDate: Long? = null
)