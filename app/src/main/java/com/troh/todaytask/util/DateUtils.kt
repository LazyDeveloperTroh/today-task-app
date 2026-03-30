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
     * 현재 날짜를 텍스트로 반환
     * ex) 2026년 3월 3일 (화)
     */
    fun getTodayText(): String {
        val sdf = SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN)
        return sdf.format(Date())
    }

    /**
     * 밀리세컨즈를 받아 해당 일의 0시 0분 0초.000 으로 리턴
     */
    fun getStartOfDayMillis(timeMillis: Long): Long {
        return Calendar.getInstance().apply { 
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * 밀리세컨즈를 받아 해당 일의 23시 59분 59초.999 으로 리턴
     */
    fun getEndOfDayMillis(timeMillis: Long): Long {
        return Calendar.getInstance().apply { 
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}