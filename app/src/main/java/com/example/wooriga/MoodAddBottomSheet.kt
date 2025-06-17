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
            val category = binding.etCategory.text.toString().trim()
            val tagInput = binding.etTag.text.toString().trim()

            if (category.isBlank()) {
                Toast.makeText(requireContext(), "감정을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tags = tagInput
                .split(",", " ", "#")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            onSubmit(category, tags)
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
