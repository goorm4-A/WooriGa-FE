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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.wooriga.databinding.BottomSheetAddDiaryBinding
import android.widget.ArrayAdapter


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
            binding.addFamilyHistoryButton.setOnClickListener {
                showAddDiaryBottomSheet()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddDiaryBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetAddDiaryBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(bottomSheetBinding.root)

        // 태그 Spinner 설정
        val tagAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tag_list,
            android.R.layout.simple_spinner_item
        )
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bottomSheetBinding.spinnerTag.adapter = tagAdapter

// 참여자 Spinner 설정
        val memberAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.member_list,
            android.R.layout.simple_spinner_item
        )
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bottomSheetBinding.spinnerMember.adapter = memberAdapter

        // 버튼 클릭 리스너 예시
        bottomSheetBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            // TODO: 일기 등록 처리
            dialog.dismiss()
        }

        dialog.show()
    }


}