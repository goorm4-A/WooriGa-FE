package com.example.wooriga

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wooriga.databinding.FragmentFamilyDiaryBinding
import com.example.wooriga.utils.ToolbarUtils

class FamilyDiaryFragment : Fragment() {

    private var _binding: FragmentFamilyDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by activityViewModels()
    private lateinit var diaryAdapter: DiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedUser = UserManager.loadUserInfo()
        // 사용자 이름을 가져와서 추억 제목에 적용
        val name = savedUser?.name ?: "이름 없음"
        val message = "${name}님의 추억들을\n관리해보세요!"

        val spannable = SpannableString(message)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.diaryTitle.text = spannable

        // 가족 선택
        ToolbarUtils.setupFamilyGroupIcon(binding.customToolbar.iconSelectFamily, requireContext()) { selectedGroup ->
            val familyId = selectedGroup.familyGroup.familyGroupId
            val familyName = selectedGroup.familyGroup.familyName

            viewModel.selectFamily(familyId)
        }

        // 상단바 검색 버튼
        binding.customToolbar.iconSearch.setOnClickListener {
            // 일기 검색 프래그먼트로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DiarySearchFragment())
                .addToBackStack(null)
                .commit()
        }

        setupRecyclerView()
        observeViewModel()
        setupListeners()

        // 최초 로딩 시 현재 선택된 가족이 있으면 불러오기
        ToolbarUtils.currentGroup?.let {
            viewModel.selectFamily(it.familyGroup.familyGroupId)
        }
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter { diary ->
            val fragment = DiaryDetailFragment().apply {
                arguments = Bundle().apply {
                    // 일기 아이디 넘겨주기
                    putLong("diaryId", diary.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }

        binding.recyclerDiary.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = diaryAdapter
            setHasFixedSize(true)
        }
    }


    private fun observeViewModel() {
        viewModel.diaryList.observe(viewLifecycleOwner) { diaries ->
            diaryAdapter.submitList(diaries)
        }
    }

    private fun setupListeners() {
        binding.addFamilyDiaryButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DiaryAddFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}