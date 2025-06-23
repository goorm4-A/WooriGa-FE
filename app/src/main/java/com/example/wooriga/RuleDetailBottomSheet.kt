package com.example.wooriga

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.wooriga.databinding.BottomSheetDetailRuleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class RuleDetailBottomSheet(
    private val ruleId: Long
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailRuleBinding? = null
    private val binding get() = _binding!!

    private val repository = RuleRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadRuleDetail()

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnEdit.setOnClickListener { dismiss() }
    }

    private fun loadRuleDetail() {
        lifecycleScope.launch {
            try {
                val response = repository.getRuleDetail(ruleId)
                if (response.isSuccess && response.result != null) {

                    // description 포함 여부 확인
                    Log.d("RuleDetail", "서버 응답 description = ${response.result.description}")

                    val rule = response.result.toUiModel()
                    binding.tvTitle.text = rule.title
                    binding.tvType.text = rule.type
                    binding.tvDate.text = rule.date
                    binding.tvFamily.text = rule.family
                    binding.tvDescription.text = rule.description // 설명
                } else {
                    Toast.makeText(context, "상세 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "서버 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
