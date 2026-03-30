package com.troh.todaytask.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun getTodayEndMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    /**
     * ex) 2026년 3월 3일 (화)
     */
    fun getTodayText(): String {
        val sdf = SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN)
        return sdf.format(Date())
    }
}