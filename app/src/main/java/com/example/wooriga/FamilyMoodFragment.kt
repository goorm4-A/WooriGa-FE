package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.FragmentFamilyMoodBinding

class FamilyMoodFragment : Fragment() {

    private var _binding: FragmentFamilyMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MoodAdapter
    private val viewModel: MoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인자로 받은 familyId 추출
        val familyId = arguments?.getLong("familyId") ?: return

        // API 호출
        viewModel.loadFamilyMoods(familyId)

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "분위기"

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 어댑터 초기화
        adapter = MoodAdapter { mood ->
            // 아이템 클릭 시 동작
        }

        binding.recyclerFamilyMood.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFamilyMood.adapter = adapter

        // ViewModel의 LiveData 관찰 → 리스트 갱신
        viewModel.moodList.observe(viewLifecycleOwner) { moodList ->
            adapter.submitList(moodList)
        }

        binding.addFamilyMood.setOnClickListener {
            MoodAddBottomSheet { category, tags ->
                val newMood = Mood(
                    id = System.currentTimeMillis(),
                    familyId = familyId,
                    moodType = category,
                    tags = tags
                )
                viewModel.addMood(newMood)
            }.show(parentFragmentManager, "MoodAddBottomSheet")
        }

    }

}