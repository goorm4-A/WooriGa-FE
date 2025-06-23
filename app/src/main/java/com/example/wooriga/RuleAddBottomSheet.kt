package com.example.wooriga

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.wooriga.databinding.BottomSheetAddRuleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RuleAddBottomSheet(
    private val familyName: String,
    private val onSubmit: (RuleRequest) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddRuleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RuleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Spinner 초기화 예시 (수동 목록)
        val typeList = listOf("필수 규칙", "권장 사항", "금기 사항")

        binding.spinnerType.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, typeList)

        // 날짜 표시
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val today = formatter.format(Date())
        binding.dateText.text = today

        // 취소 버튼
        binding.btnCancel.setOnClickListener { dismiss() }

        // 제출 버튼
        binding.btnSubmit.setOnClickListener {
            val ruleTypeKor = binding.spinnerType.selectedItem.toString()
            val title = binding.etRule.text.toString()
            val description = binding.etDescription.text.toString()

            if (title.isNotBlank()) {
                val request = RuleRequest(
                    familyName = familyName,
                    ruleType = when (ruleTypeKor) {
                        "필수 규칙" -> "REQUIRED"
                        "권장 사항" -> "RECOMMENDED"
                        "금기 사항" -> "PROHIBITED"
                        else -> "REQUIRED"
                    },
                    title = title,
                    description = description
                )
                onSubmit(request)
                dismiss()
            } else {
                Toast.makeText(context, "약속 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
