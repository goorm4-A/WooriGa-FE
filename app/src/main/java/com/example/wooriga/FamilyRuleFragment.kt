package com.example.wooriga

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wooriga.databinding.FragmentFamilyRuleBinding

class FamilyRuleFragment : Fragment() {

    private var _binding: FragmentFamilyRuleBinding? = null
    private val binding get() = _binding!!

    private val ruleList = mutableListOf<Rule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyRuleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "규칙"

        // 초기 규칙 샘플 추가
        ruleList.addAll(
            listOf(
                Rule("A가족", "필수 규칙", "착하게 살자.", "서로 예의를 지키자", "2025년 4월 12일 토요일"),
                Rule("A가족", "권장 사항", "일찍 일어나기", "건강한 생활 습관", "2025년 4월 13일 일요일"),
                Rule("B가족", "금기 사항", "욕하지 않기", "언제나 존중하는 말 사용", "2025년 4월 11일 금요일")
            )
        )

        updateUI()

        binding.addFamilyRule.setOnClickListener {
            RuleAddBottomSheet { newRule ->
                ruleList.add(newRule)
                updateUI()
            }.show(parentFragmentManager, "AddRuleBottomSheet")
        }

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateUI() {
        binding.ruleContainer.removeAllViews()

        val grouped = ruleList.groupBy { it.type }

        grouped.forEach { (type, items) ->
            addRuleSection(binding.ruleContainer, type, items)
        }
    }

    private fun addRuleSection(container: LinearLayout, title: String, items: List<Rule>) {
        val context = requireContext()

        val titleView = TextView(context).apply {
            text = title
            textSize = 14f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(36, 24, 0, 12)
            }
            setPadding(24, 12, 24, 12)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 24f
                setColor(Color.parseColor("#D4E9A4"))
            }
        }

        container.addView(titleView)

        for (rule in items) {
            val itemView = layoutInflater.inflate(R.layout.item_family_motto, container, false)
            val ruleText = itemView.findViewById<TextView>(R.id.text_motto)
            val dateText = itemView.findViewById<TextView>(R.id.text_date)

            ruleText.text = rule.title
            dateText.text = rule.date

            // 아이템 클릭시 규칙 상세 바텀시트
            itemView.setOnClickListener {
                RuleDetailBottomSheet(rule).show(parentFragmentManager, "RuleDetailBottomSheet")
            }

            container.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}