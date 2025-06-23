package com.example.wooriga

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.FragmentFamilyRuleBinding

class FamilyRuleFragment : Fragment() {

    private var _binding: FragmentFamilyRuleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RuleViewModel by viewModels()
    private lateinit var adapter: RuleListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFamilyRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedUser = UserManager.loadUserInfo()
        val userId = savedUser?.userId

        // 인자로 받은 familyId 추출
        val familyId = arguments?.getLong("familyId") ?: return
        val familyName = arguments?.getString("familyName").orEmpty()

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "규칙"

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = RuleListAdapter { rule ->
            RuleDetailBottomSheet(rule.id).show(parentFragmentManager, "RuleDetailBottomSheet")
        }


        binding.recyclerFamilyRule.adapter = adapter
        binding.recyclerFamilyRule.layoutManager = LinearLayoutManager(requireContext())

        viewModel.ruleList.observe(viewLifecycleOwner) { rules ->
            val grouped = rules.groupBy { it.type }
            val list = mutableListOf<RuleListItem>()
            grouped.forEach { (type, rules) ->
                list.add(RuleListItem.Header(type))
                list.addAll(rules.map { RuleListItem.Content(it) })
            }
            adapter.submitList(list)
        }

        // 데이터 불러오기
        if (userId != null) {
            viewModel.loadRules(familyId = familyId, userId = userId)
        } else {
            Log.e("FamilyRuleFragment", "userId is null - 로그인 필요")
        }

        binding.addFamilyRule.setOnClickListener {
            RuleAddBottomSheet(familyName) { request ->
                viewModel.addRuleRemote(request)
            }.show(parentFragmentManager, "AddRuleBottomSheet")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
