package com.example.wooriga

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.FragmentFamilyDiaryBinding
import com.example.wooriga.ui.familydiary.FamilyDiaryViewModel
import com.example.wooriga.ui.familydiary.DiaryAdapter
import androidx.recyclerview.widget.GridLayoutManager

class FamilyDiaryFragment : Fragment() {

    private var _binding: FragmentFamilyDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FamilyDiaryViewModel by viewModels()
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

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter()
        binding.recyclerDiary.apply {
            layoutManager = GridLayoutManager(requireContext(), 2) // 열 수: 2
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
        binding.addFamilyHistoryButton.setOnClickListener {
            // 예: 다이어리 작성 화면으로 이동
            // findNavController().navigate(R.id.action_familyDiaryFragment_to_addDiaryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}