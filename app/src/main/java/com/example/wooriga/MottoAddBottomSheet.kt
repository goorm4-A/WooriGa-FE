package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetAddMottoBinding
import com.example.wooriga.utils.ToolbarUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MottoAddBottomSheet(
    private val onSubmit: (familyId: Long, familyName: String, motto: String) -> Unit
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

        // 오늘 날짜 표시
        binding.dateText.text = getTodayFormatted()

        binding.btnSubmit.setOnClickListener {
            val motto = binding.etMotto.text.toString().trim()

            val selectedGroup = ToolbarUtils.currentGroup
            if (selectedGroup == null) {
                Toast.makeText(context, "선택된 가족이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (motto.isBlank()) {
                Toast.makeText(context, "가훈 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSubmit(
                selectedGroup.familyGroup.familyGroupId,
                selectedGroup.familyGroup.familyName,
                motto
            )
            dismiss()
        }

        // 취소 버튼 리스너
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun getTodayFormatted(): String {
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        return formatter.format(today)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
