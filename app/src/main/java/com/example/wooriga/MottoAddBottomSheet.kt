package com.example.wooriga

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetAddMottoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MottoAddBottomSheet(
    private val onSubmit: (familyName: String, motto: String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddMottoBinding? = null
    private val binding get() = _binding!!

    private val familyList = listOf("A가족", "B가족", "C가족") // TODO: 서버에서 가족 목록 받아오기

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddMottoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 가족 목록 바인딩
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, familyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTag.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            val familyName = binding.spinnerTag.selectedItem.toString()
            val motto = binding.etMotto.text.toString()

            if (familyName.isNotBlank() && motto.isNotBlank()) {
                onSubmit(familyName, motto)
                dismiss()
            } else {
                Toast.makeText(context, "모든 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼 리스너
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
