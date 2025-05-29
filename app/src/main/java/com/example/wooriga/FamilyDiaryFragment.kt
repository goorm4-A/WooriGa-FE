package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wooriga.databinding.BottomSheetAddDiaryBinding
import com.example.wooriga.databinding.FragmentFamilyDiaryBinding
import com.example.wooriga.Diary
import com.google.android.material.bottomsheet.BottomSheetDialog


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

        // 취소 버튼 클릭 리스너
        bottomSheetBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // 추가 버튼 클릭 리스너
        bottomSheetBinding.submitButton.setOnClickListener {
            val title = bottomSheetBinding.titleInput.text.toString()
            val location = bottomSheetBinding.locationInput.text.toString()
            val content = bottomSheetBinding.memoInput.text.toString()
            val date = bottomSheetBinding.dateText.text.toString()
            val tag = bottomSheetBinding.spinnerTag.selectedItem.toString() // TODO: selectedTags 다중 선택된 태그
            val member = bottomSheetBinding.spinnerMember.selectedItem.toString() // TODO: selectedTags 다중 선택된 태그

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(requireContext(), "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val diary = Diary(
                date = date,
                imageUri = null, // TODO: 이미지 URI 처리 시 적용
                title = title,
                tag = tag,
                member = member,
                location = location,
                content = content
            )

            viewModel.addDiary(diary)
            dialog.dismiss()
        }


        dialog.show()
    }


}