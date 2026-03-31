package com.troh.todaytask.feature.history.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.troh.todaytask.R
import com.troh.todaytask.data.local.AppDatabase
import com.troh.todaytask.databinding.FragmentHistoryBinding
import com.troh.todaytask.feature.history.HistoryAdapter
import com.troh.todaytask.feature.history.HistoryUiMapper
import com.troh.todaytask.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private var startDateMillis: Long = DateUtils.getDaysAgoStartMillis(7)
    private var endDateMillis: Long = DateUtils.getTodayEndMillis()

    private lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)

        initViews()
        bindClicks()
        loadHistory()
    }

    private fun initViews() {
        binding.tvStartDate.text = DateUtils.formatDate(startDateMillis)
        binding.tvEndDate.text = DateUtils.formatDate(endDateMillis)

        historyAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun bindClicks() {
        binding.layoutStartDate.setOnClickListener {
            showDatePicker(startDateMillis) { selected ->
                startDateMillis = DateUtils.getStartOfDayMillis(selected)
                binding.tvStartDate.text = DateUtils.formatDate(startDateMillis)
            }
        }

        binding.layoutEndDate.setOnClickListener {
            showDatePicker(endDateMillis) { selected ->
                endDateMillis = DateUtils.getEndOfDayMillis(selected)
                binding.tvEndDate.text = DateUtils.formatDate(endDateMillis)
            }
        }

        binding.btnSearch.setOnClickListener {
            loadHistory()
        }
    }

    private fun showDatePicker(currentMillis: Long, onSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentMillis
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                onSelected(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            val todos = db.todoDao().getTodosByScheduledDate(startDateMillis, endDateMillis)
            val groupedItems = HistoryUiMapper.map(todos)
            historyAdapter.submitList(groupedItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}