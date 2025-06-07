package com.example.wooriga

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.BottomSheetAddAnniversaryBinding
import com.example.wooriga.databinding.BottomSheetDetailAnniversaryBinding
import com.example.wooriga.databinding.FragmentFamilyAnniversaryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.wooriga.model.Anniversary

class FamilyAnniversaryFragment : Fragment() {

    private var _binding: FragmentFamilyAnniversaryBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: AnniversaryAdapter
    private val repository = AnniversaryRepository
    private val allAnnivList = mutableListOf<Anniversary>()

    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyAnniversaryBinding.inflate(inflater, container, false)

        repository.loadSampleData()
        allAnnivList.addAll(repository.getAll())

        // RecyclerView 설정
        adapter = AnniversaryAdapter(allAnnivList, ::onAnnivItemClicked)
        binding.annivListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.annivListRecyclerView.adapter = adapter

        // 캘린더 클릭 -> 일정 추가
        binding.calendarAnniv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 날짜 저장 (month는 0부터 시작하므로 +1 해줌)
            selectedDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            showBottomSheetDialog(selectedDate, null)  // null이면 새 일정
        }

        // 검색
        binding.customToolbar.iconSearch.setOnClickListener {
            // detail 액티비티로 이동
            val intent = Intent(requireContext(), AnniversaryDetailActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 기념일 화면 타이틀
        val name = "송이"
        val message = "${name}님의 기념일들을\n관리해보세요!"

        val spannable = SpannableString(message)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.annivTitle.text = spannable

    }

        private fun onAnnivItemClicked(anniv: Anniversary) {
            showAnniversaryDetailDialog(anniv)
        }


    // 기념일 등록 다이얼로그
    private fun showBottomSheetDialog(date: String, annivToEdit: Anniversary?) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetAddAnniversaryBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(bottomSheetBinding.root)

        val titleInput = bottomSheetBinding.titleInput
        val spinner = bottomSheetBinding.tagInput
        val locationInput = bottomSheetBinding.locationInput
        val memoInput = bottomSheetBinding.memoInput
        val submitButton = bottomSheetBinding.submitButton
        val cancelButton = bottomSheetBinding.cancelButton
        val dateView = bottomSheetBinding.dateText

        dateView.text = date

        val types = listOf("태그", "경조사", "생일", "약속", "기타")
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        // 기존 일정 -> 데이터 세팅
        if (annivToEdit != null) {
            titleInput.setText(annivToEdit.title)
            val pos = types.indexOf(annivToEdit.tag)
            if (pos >= 0) spinner.setSelection(pos)
            locationInput.setText(annivToEdit.location)
            memoInput.setText(annivToEdit.memo)
            submitButton.text = "수정"
        }

        submitButton.setOnClickListener {
            val inputTitle = titleInput.text.toString().trim()
            val inputTag = spinner.selectedItem.toString()
            val inputLocation = locationInput.text.toString().trim()
            val inputMemo = memoInput.text.toString().trim()

            if (inputTitle.isEmpty() || inputTag == "태그") {
                Toast.makeText(requireContext(), "제목과 태그를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (annivToEdit != null) {
                // 수정: 기존 데이터 수정 후 리스트 갱신
                annivToEdit.title = inputTitle
                annivToEdit.tag = inputTag
                annivToEdit.location = inputLocation
                annivToEdit.memo = inputMemo

                // repository.updateAnniversary(annivToEdit)
            } else {
                // 새로 추가
                val newAnniv = Anniversary(date, inputTitle, inputTag, inputLocation, inputMemo)
                repository.addAnniversary(newAnniv)
                allAnnivList.add(newAnniv)
            }

            adapter.updateList(allAnnivList) // 추가 및 수정 후 반드시 갱신 호출
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }


    // 상세보기 다이얼로그
    private fun showAnniversaryDetailDialog(anniv: Anniversary) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetDetailAnniversaryBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.dateOutput.text = anniv.date
        bottomSheetBinding.titleOutput.text = anniv.title
        bottomSheetBinding.tagOutput.text = anniv.tag
        bottomSheetBinding.locationOutput.text = anniv.location
        bottomSheetBinding.memoOutput.text = anniv.memo

        // 닫기 버튼
        val closeButton =bottomSheetBinding.closeButton
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        
        // 수정 버튼
        val editButton = bottomSheetBinding.editButton
        editButton.setOnClickListener {
            dialog.dismiss()  // 상세보기 다이얼로그 닫기
            showBottomSheetDialog(anniv.date, anniv) // 수정 화면으로 이동
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지
    }
}