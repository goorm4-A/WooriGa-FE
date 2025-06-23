package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wooriga.databinding.BottomSheetEditMottoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MottoEditBottomSheet(
    private val motto: Motto,
    private val viewModel: MottoViewModel
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditMottoBinding? = null
    private val binding get() = _binding!!

    val savedUser = UserManager.loadUserInfo()
    val userId = savedUser?.userId

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
        binding.dateText.text = DateUtils.formatIsoDate(motto.createdAt)

        // EditText 초기값
        binding.titleInput.setText(motto.title)

        // 확인 버튼
        binding.btnSubmit.setOnClickListener {
            val newTitle = binding.titleInput.text.toString()

            if (newTitle.isNotBlank()) {
                userId?.let {
                    viewModel.editMotto(
                        familyId = motto.familyId,
                        mottoId = motto.id,
                        userId = it,
                        familyName = motto.familyName,
                        motto = newTitle
                    )
                    dismiss()
                } ?: Toast.makeText(context, "로그인 필요", Toast.LENGTH_SHORT).show()
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
