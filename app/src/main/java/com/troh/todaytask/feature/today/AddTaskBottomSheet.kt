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

        binding.etTask.requestFocus()
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
        if(text.isEmpty()) return

        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY_TASK to text)
        )
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
    }
}