package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wooriga.databinding.BottomSheetDetailMottoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MottoDetailBottomSheet(
    private val motto: Motto
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailMottoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailMottoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 바인딩 데이터 설정
        //binding.familyOutput.text = motto.familyName
        binding.mottoOutput.text = motto.title
        binding.dateText.text = DateUtils.formatIsoDate(motto.createdAt)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
