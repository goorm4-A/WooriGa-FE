package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wooriga.databinding.FragmentFamilyCultureBinding
import com.example.wooriga.utils.ToolbarUtils
import com.example.wooriga.utils.ToolbarUtils.currentGroup

class FamilyCultureFragment : Fragment() {

    private var _binding: FragmentFamilyCultureBinding? = null
    private val binding get() = _binding!!

    private var selected = currentGroup?.familyGroup
    private var selectedFamilyId: Long = selected?.familyGroupId ?: -1L
    private var selectedFamilyName: String = selected?.familyName.orEmpty()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyCultureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 가족 선택 아이콘 클릭
        ToolbarUtils.setupFamilyGroupIcon(binding.customToolbar.iconSelectFamily, requireContext()) { selectedGroup ->
            selected = selectedGroup.familyGroup
            selectedFamilyId = selected?.familyGroupId ?: -1L
            selectedFamilyName = selected?.familyName.orEmpty()

            Toast.makeText(requireContext(), "$selectedFamilyName 선택됨", Toast.LENGTH_SHORT).show()
        }

        // 가훈 화면으로 이동
        binding.itemCultureMotto.buttonMotto.setOnClickListener {
            navigateToFragment(FamilyMottoFragment(), "familyId" to selectedFamilyId)
        }

        // 분위기 화면으로 이동
        binding.itemCultureMood.buttonMood.setOnClickListener {
            navigateToFragment(FamilyMoodFragment(), "familyId" to selectedFamilyId)
        }

        // 규칙 화면으로 이동
        binding.itemCultureRule.buttonRule.setOnClickListener {
            navigateToFragment(FamilyRuleFragment(), "familyId" to selectedFamilyId)
        }

        // 요리법 화면으로 이동
        binding.itemCultureRecipe.buttonRecipe.setOnClickListener {
            navigateToFragment(RecipeFragment(), "familyId" to selectedFamilyId)
        }
    }

    private fun navigateToFragment(fragment: Fragment, vararg args: Pair<String, Long>) {
        if (selectedFamilyId == -1L) {
            Toast.makeText(requireContext(), "가족을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE

        fragment.arguments = Bundle().apply {
            args.forEach { putLong(it.first, it.second) }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
