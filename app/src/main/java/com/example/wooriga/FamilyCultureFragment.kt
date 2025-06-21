package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.wooriga.databinding.FragmentFamilyCultureBinding

class FamilyCultureFragment : Fragment() {

    private var _binding: FragmentFamilyCultureBinding? = null
    private val binding get() = _binding!!

    // 선택된 가족
    private var selectedFamilyId: Long = -1L
    private var selectedFamilyName: String = ""

    // TODO: 추후 ViewModel로 변경
    // TODO: Family 모델 삭제
    val families = listOf(
        Family(familyId = 1, name = "A가족"),
        Family(familyId = 2, name = "B가족"),
        Family(familyId = 3, name = "C가족")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyCultureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFamilyCultureBinding.bind(view)

        // 디폴트로 첫 번째 가족 선택
        val defaultFamily = families.first()
        selectedFamilyId = defaultFamily.familyId
        selectedFamilyName = defaultFamily.name

        // 가족 선택 아이콘 클릭
        val iconMenu = view.findViewById<Toolbar>(R.id.custom_toolbar).findViewById<ImageView>(R.id.icon_menu)
        iconMenu.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            families.forEachIndexed { index, family ->
                popup.menu.add(0, index, 0, family.name)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedFamily = families[menuItem.itemId]
                selectedFamilyId = selectedFamily.familyId
                selectedFamilyName = selectedFamily.name

                Toast.makeText(requireContext(), "${selectedFamily.name} 선택됨", Toast.LENGTH_SHORT).show()
                true
            }

            popup.show()
        }


        val buttonMotto = view.findViewById<ImageButton>(R.id.button_motto)
        buttonMotto.setOnClickListener {
            // 하단 네비게이션 숨기기
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE
            // 선택된 가족 아이디 넘기기
            val fragment = FamilyMottoFragment().apply {
                arguments = Bundle().apply {
                    putLong("familyId", selectedFamilyId)
                }
            }
            // 가훈 프래그먼트로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val buttonMood = view.findViewById<ImageButton>(R.id.button_mood)
        buttonMood.setOnClickListener {
            // 하단 네비게이션 숨기기
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE
            // 선택된 가족 아이디 넘기기
            val fragment = FamilyMoodFragment().apply {
                arguments = Bundle().apply {
                    putLong("familyId", selectedFamilyId)
                }
            }
            // 분위기 프래그먼트로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val buttonRule = view.findViewById<ImageButton>(R.id.button_rule)
        buttonRule.setOnClickListener {
            // 하단 네비게이션 숨기기
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE
            // 선택된 가족 아이디 넘기기
            val fragment = FamilyRuleFragment().apply {
                arguments = Bundle().apply {
                    putLong("familyId", selectedFamilyId)
                }
            }
            // 규칙 프래그먼트로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val buttonRecipe = view.findViewById<ImageButton>(R.id.button_recipe)
        buttonRecipe.setOnClickListener {
            // 하단 네비게이션 숨기기
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE
            // 선택된 가족 아이디 넘기기
            val fragment = RecipeFragment().apply {
                arguments = Bundle().apply {
                    putLong("familyId", selectedFamilyId)
                }
            }
            // 요리법 프래그먼트로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

}