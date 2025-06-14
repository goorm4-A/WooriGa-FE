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
import androidx.fragment.app.viewModels
import com.example.wooriga.databinding.FragmentFamilyRuleBinding

class FamilyRuleFragment : Fragment() {

    private var _binding: FragmentFamilyRuleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RuleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "규칙"

        // ViewModel의 ruleList 관찰
        viewModel.ruleList.observe(viewLifecycleOwner) { rules ->
            updateUI(rules)
        }

        // 규칙 추가 버튼 클릭 시
        binding.addFamilyRule.setOnClickListener {
            RuleAddBottomSheet { newRule ->
                viewModel.addRule(newRule)
            }.show(parentFragmentManager, "AddRuleBottomSheet")
        }

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateUI(ruleList: List<Rule>) {
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
