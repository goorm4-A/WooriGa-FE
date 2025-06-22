package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        // 어댑터 초기화 + 삭제 처리
        adapter = MoodAdapter(
            onItemClick = { mood ->
                // 분위기 아이템 클릭 처리 (필요 시)
            },
            onDeleteClick = { mood ->
                AlertDialog.Builder(requireContext())
                    .setTitle("분위기 삭제")
                    .setMessage("이 분위기를 삭제할까요?")
                    .setPositiveButton("삭제") { _, _ ->
                        viewModel.deleteMood(
                            familyId = familyId,
                            moodId = mood.id,
                            onSuccess = {
                                Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        )


        binding.recyclerFamilyMood.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFamilyMood.adapter = adapter

        // ViewModel의 LiveData 관찰 → 리스트 갱신
        viewModel.moodList.observe(viewLifecycleOwner) { moodList ->
            adapter.submitList(moodList)
        }

        binding.addFamilyMood.setOnClickListener {
            MoodAddBottomSheet { category, tags ->
                viewModel.postMood(
                    familyId = familyId,
                    moodType = category, // 예: "EMOTION"
                    tags = tags,
                    onSuccess = {
                        Toast.makeText(requireContext(), "분위기가 등록되었어요.", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "분위기 등록에 실패했어요.", Toast.LENGTH_SHORT).show()
                    }
                )
            }.show(parentFragmentManager, "MoodAddBottomSheet")
        }


    }

}