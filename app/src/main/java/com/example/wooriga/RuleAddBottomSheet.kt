package com.example.wooriga

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.wooriga.databinding.BottomSheetAddRuleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RuleAddBottomSheet(
    private val onSubmit: (Rule) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddRuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Spinner 초기화 예시 (수동 목록)
        val familyList = listOf("A 가족", "B 가족")
        val typeList = listOf("필수 규칙", "권장 사항", "금기 사항")

        binding.spinnerFamily.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, familyList)
        binding.spinnerType.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, typeList)

        // 날짜 표시
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val today = formatter.format(Date())
        binding.dateText.text = today

        // 취소 버튼
        binding.btnCancel.setOnClickListener { dismiss() }

        // 제출 버튼
        binding.btnSubmit.setOnClickListener {
            val rule = Rule(
                family = binding.spinnerFamily.selectedItem.toString(),
                type = binding.spinnerType.selectedItem.toString(),
                title = binding.etRule.text.toString(),
                description = binding.etDescription.text.toString(),
                date = binding.dateText.text.toString()
            )
            onSubmit(rule)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
