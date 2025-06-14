package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        adapter = RuleListAdapter { rule ->
            RuleDetailBottomSheet(rule).show(parentFragmentManager, "RuleDetailBottomSheet")
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

        binding.addFamilyRule.setOnClickListener {
            RuleAddBottomSheet { newRule ->
                viewModel.addRule(newRule)
            }.show(parentFragmentManager, "AddRuleBottomSheet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
