package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetEditMottoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MottoEditBottomSheet(
    private val motto: Motto,
    private val viewModel: MottoViewModel
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditMottoBinding? = null
    private val binding get() = _binding!!

    private val familyOptions = listOf("A가족", "B가족", "C가족") // 예시 태그

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditMottoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 날짜 표시
        binding.dateText.text = motto.createdAt

        // EditText 초기값
        binding.titleInput.setText(motto.title)

        // Spinner 설정
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, familyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTag.adapter = adapter
        val position = familyOptions.indexOf(motto.familyName)
        if (position >= 0) binding.spinnerTag.setSelection(position)

        // 확인 버튼
        binding.btnSubmit.setOnClickListener {
            val newTitle = binding.titleInput.text.toString()
            val newFamily = binding.spinnerTag.selectedItem.toString()

            if (newTitle.isNotBlank()) {
                viewModel.editMotto(motto.id, newFamily, newTitle)
                dismiss()
            } else {
                Toast.makeText(context, "가훈을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
