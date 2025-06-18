package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wooriga.databinding.FragmentFamilyDiaryBinding


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

        // 검색
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

        viewModel.loadDiaries()
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