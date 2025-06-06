package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetAddMottoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMottoBottomSheet(
    private val onSubmit: (familyName: String, motto: String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddMottoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddMottoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            val familyName = binding.etFamilyName.text.toString()
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
