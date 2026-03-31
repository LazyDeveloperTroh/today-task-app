package com.troh.todaytask.feature.today

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY id DESC")
    suspend fun getAllTodos(): List<TodoEntity>

    @Insert
    suspend fun insert(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Query("""
        SELECT *
        FROM todo
        WHERE scheduledDate BETWEEN :startOfDay AND :endOfDay
        ORDER BY scheduledDate DESC, id DESC
    """)
    suspend fun getTodosByScheduledDate(startOfDay: Long, endOfDay: Long): List<TodoEntity>
}