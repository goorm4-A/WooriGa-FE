package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wooriga.databinding.BottomSheetDetailRuleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RuleDetailBottomSheet(
    private val rule: Rule
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailRuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.familyOutput.text = rule.family
        binding.typeOutput.text = rule.type
        binding.ruleOutput.text = rule.title
        binding.descriptionOutput.text = rule.description
        binding.dateText.text = rule.date

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnEdit.setOnClickListener {
            // TODO: 편집 바텀시트 열기
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
