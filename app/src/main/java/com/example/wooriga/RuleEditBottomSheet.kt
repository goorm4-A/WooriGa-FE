package com.example.wooriga

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetAddRuleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RuleEditBottomSheet(
    private val rule: Rule,
    private val onUpdate: (ruleId: Long, RuleRequest) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddRuleBinding? = null
    private val binding get() = _binding!!

    private val typeList = listOf("필수 규칙", "권장 사항", "금기 사항")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 스피너 초기화
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, typeList)
        binding.spinnerType.adapter = spinnerAdapter
        binding.spinnerType.setSelection(typeList.indexOf(rule.type))

        // 기존 내용 세팅
        binding.tvTitle.text = "규칙 수정"
        binding.etRule.setText(rule.title)
        binding.etDescription.setText(rule.description)
        binding.dateText.text = rule.date
        binding.btnSubmit.text = "수정"

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSubmit.setOnClickListener {
            val selectedType = binding.spinnerType.selectedItem.toString()
            val title = binding.etRule.text.toString()
            val desc = binding.etDescription.text.toString()

            if (title.isNotBlank()) {
                val request = RuleRequest(
                    familyName = rule.family,
                    ruleType = when (selectedType) {
                        "필수 규칙" -> "REQUIRED"
                        "권장 사항" -> "RECOMMENDED"
                        "금기 사항" -> "PROHIBITED"
                        else -> "RECOMMENDED"
                    },
                    title = title,
                    description = desc
                )
                onUpdate(rule.id, request)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
