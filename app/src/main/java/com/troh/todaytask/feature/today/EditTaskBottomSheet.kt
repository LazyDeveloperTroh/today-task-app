package com.troh.todaytask.feature.today

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.troh.todaytask.R
import com.troh.todaytask.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditTaskBottomSheet(
    private val todo: TodoEntity,
    private val onSaveClick: (TodoEntity) -> Unit
) : BottomSheetDialogFragment(R.layout.bottom_sheet_edit_task) {

    private lateinit var btnClose: ImageButton
    private lateinit var etTask: TextInputEditText
    private lateinit var layoutScheduledDate: View
    private lateinit var tvScheduledDate: TextView
    private lateinit var btnSave: MaterialButton

    private var selectedScheduledDate: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        initData()
        bindClicks()
    }

    private fun bindViews(view: View) {
        btnClose = view.findViewById(R.id.btnClose)
        etTask = view.findViewById(R.id.etTask)
        layoutScheduledDate = view.findViewById(R.id.layoutScheduledDate)
        tvScheduledDate = view.findViewById(R.id.tvScheduledDate)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun initData() {
        android.util.Log.d("EditTaskBottomSheet", "todo.title = ${todo.title}")

        etTask.setText(todo.title)
        selectedScheduledDate = todo.scheduledDate ?: DateUtils.getTodayEndMillis()
        updateScheduledDateText()
    }

    private fun bindClicks() {
        btnClose.setOnClickListener {
            dismiss()
        }

        layoutScheduledDate.setOnClickListener {
            showScheduledDatePicker()
        }

        tvScheduledDate.setOnClickListener {
            showScheduledDatePicker()
        }

        btnSave.setOnClickListener {
            saveTodo()
        }
    }

    private fun saveTodo() {
        val title = etTask.text?.toString()?.trim().orEmpty()

        if (title.isEmpty()) {
            etTask.error = "할 일을 입력해주세요"
            return
        }

        val updatedTodo = todo.copy(
            title = title,
            scheduledDate = selectedScheduledDate
        )

        onSaveClick(updatedTodo)
        dismiss()
    }

    private fun showScheduledDatePicker() {
        val calendar = Calendar.getInstance()

        if (selectedScheduledDate != null) {
            calendar.timeInMillis = selectedScheduledDate!!
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                selectedScheduledDate = selectedCalendar.timeInMillis
                updateScheduledDateText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateScheduledDateText() {
        tvScheduledDate.text = selectedScheduledDate?.let { formatDate(it) } ?: "날짜 선택"
    }

    private fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}