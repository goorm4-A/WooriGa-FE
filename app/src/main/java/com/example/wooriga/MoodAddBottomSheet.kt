package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetAddMoodBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoodAddBottomSheet(
    private val onSubmit: (emotion: String, tags: List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddMoodBinding? = null
    private val binding get() = _binding!!

    private val moodTypeMap = mapOf(
        "감정" to "EMOTION",
        "성격" to "TRAIT",
        "가치관" to "VALUE"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 추가 버튼 클릭
        binding.btnSubmit.setOnClickListener {
            val selectedKorean = binding.spinnerCategory.selectedItem.toString()
            val moodType = moodTypeMap[selectedKorean]

            if (moodType == null) {
                Toast.makeText(requireContext(), "분류를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tagInput = binding.etTag.text.toString().trim()
            val tags = tagInput
                .split(",", " ", "#")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (tags.isEmpty()) {
                Toast.makeText(requireContext(), "태그를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSubmit(moodType, tags) // 영문 ENUM으로 전달
            dismiss()
        }

        // 취소 버튼 클릭
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
