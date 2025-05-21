package com.example.wooriga

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.FragmentFamilyAnniversaryBinding
import com.example.wooriga.databinding.FragmentFamilyHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

data class TimelineEvent(
    val date: String,
    val title: String,
    val location: String
)

class FamilyHistoryFragment : Fragment() {

    private lateinit var timelineRecyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter
    private val events = mutableListOf<TimelineEvent>()

    private var _binding: FragmentFamilyHistoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFamilyHistoryBinding.inflate(inflater, container, false)

        timelineRecyclerView = binding.timelineRecyclerView
        adapter = TimelineAdapter(events)
        timelineRecyclerView.adapter = adapter
        timelineRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // "+" 버튼 클릭 -> 가족사 추가 (다이얼로그)
        val addHistoryButton = binding.addFamilyHistoryButton
        addHistoryButton.setOnClickListener {
            showHistoryBottomSheetDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 가족 선택
        val spinner: Spinner = binding.selectFamilyHistory
        val items = listOf("가족 선택", "A 가족", "B 가족", "C 가족")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selected = items[position]
                if (position != 0) {
                    Toast.makeText(requireContext(), "선택한 항목: $selected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택되지 않았을 때
            }
        }

        // 가족사 화면 타이틀
        val name = "송이"
        val message = "${name}님의 기념일들을\n관리해보세요!"

        val spannable = SpannableString(message)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.historyTitle.text = spannable
    }

    // 타임라인 항목 추가 함수
    private fun addTimelineEvent(event: TimelineEvent) {
        events.add(event)
        adapter.notifyItemInserted(events.size - 1)
        timelineRecyclerView.scrollToPosition(events.size - 1)
    }

    // 가족사 등록 다이얼로그
    private fun showHistoryBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme) // 스타일 적용
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_history, null)

        val dateInput = view.findViewById<TextView>(R.id.dateInput)
        val dateOutput = view.findViewById<TextView>(R.id.dateOutput)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val locationInput = view.findViewById<EditText>(R.id.locationInput)

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val addButton = view.findViewById<Button>(R.id.submitButton)

        // 날짜 선택
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%04d.%02d.%02d", year, month + 1, dayOfMonth)
                    dateOutput.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        // 위치 선택 or 위치 작성..


        // 추가 버튼
        addButton.setOnClickListener {
            val title = titleInput.text.toString()
            val location = locationInput.text.toString()
            val date = dateOutput.text.toString()

            if (title.isNotBlank() && location.isNotBlank()) {
                val event = TimelineEvent(date = date, title = title, location = location)
                addTimelineEvent(event)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        view.post {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        dialog.show()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    
    // 날짜 별로 정렬



}
