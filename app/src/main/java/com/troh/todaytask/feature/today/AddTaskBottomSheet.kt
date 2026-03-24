package com.troh.todaytask.feature.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.troh.todaytask.R
import com.troh.todaytask.databinding.BottomSheetAddTaskBinding

class AddTaskBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    /**
     * bottom_sheet_add_task.xml 을 inflate 해서 객체로 만듦
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 레이아웃이 다 만들어진 뒤 호출
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskText = arguments?.getString(ARG_TASK_TEXT).orEmpty()
        val isEditMode = arguments?.containsKey(ARG_TASK_ID) == true

        binding.etTask.setText(taskText)
        binding.etTask.requestFocus()
        binding.etTask.setSelection(taskText.length)
        binding.btnSave.text = if(isEditMode) "수정" else "저장"

        binding.btnSave.setOnClickListener {
            saveTask()
        }
        binding.etTask.setOnEditorActionListener { _binding,  actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                saveTask()
                true
            } else {
                false
            }
        }
    }

    private fun saveTask() {
        val text = binding.etTask.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return

        val result = bundleOf(BUNDLE_KEY_TASK to text)

        if (arguments?.containsKey(ARG_TASK_ID) == true) {
            result.putLong(BUNDLE_KEY_TASK_ID, arguments?.getLong(ARG_TASK_ID) ?: -1L)
        }

        parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddTaskBottomSheet"
        const val REQUEST_KEY = "add_task_request"
        const val BUNDLE_KEY_TASK = "task"

        /**
         * 수정모드 비교를 위한 TASK ID
         */
        const val BUNDLE_KEY_TASK_ID = "task_id"
        private const val ARG_TASK_ID = "arg_task_id"
        private const val ARG_TASK_TEXT = "arg_task_text"

        fun newInstance(taskId: Long? = null, taskText: String = ""): AddTaskBottomSheet {
            return AddTaskBottomSheet().apply {
                arguments = bundleOf(ARG_TASK_TEXT to taskText).apply {
                    if (taskId != null) {
                        putLong(ARG_TASK_ID, taskId)
                    }
                }
            }
        }
    }
}