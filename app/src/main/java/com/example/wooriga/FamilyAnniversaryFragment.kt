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
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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


        // 가족 선택
        /*
        val spinner: Spinner = binding.selectFamilyAnniv
        val items = listOf("가족 선택", "A 가족", "B 가족", "C 가족")
        val adapterSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selected = items[position]
                if (position != 0) {
                    Toast.makeText(requireContext(), "선택한 항목: $selected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택되지 않았을 때
            }
        }
        */

    }

        private fun onAnnivItemClicked(anniv: Anniversary) {
            showAnniversaryDetailDialog(anniv)
        }


    // 기념일 등록 다이얼로그
    private fun showBottomSheetDialog(date: String, annivToEdit: Anniversary?) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme) // 스타일 적용
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_anniversary, null)

        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val spinner = view.findViewById<Spinner>(R.id.tagInput)
        val locationInput = view.findViewById<EditText>(R.id.locationInput)
        val memoInput = view.findViewById<EditText>(R.id.memoInput)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val dateView = view.findViewById<TextView>(R.id.dateText)

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

        dialog.setContentView(view)
        view.post {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        dialog.show()
    }


    // 상세보기 다이얼로그
    private fun showAnniversaryDetailDialog(anniv: Anniversary) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_detail_anniversary, null)

        view.findViewById<TextView>(R.id.dateOutput).text = anniv.date
        view.findViewById<TextView>(R.id.titleOutput).text = anniv.title
        view.findViewById<TextView>(R.id.tagOutput).text = anniv.tag
        view.findViewById<TextView>(R.id.locationOutput).text = anniv.location
        view.findViewById<TextView>(R.id.memoOutput).text = anniv.memo

        // 닫기 버튼
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        
        // 수정 버튼
        val editButton = view.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            dialog.dismiss()  // 상세보기 다이얼로그 닫기
            showBottomSheetDialog(anniv.date, anniv) // 수정 화면으로 이동
        }


        dialog.setContentView(view)
        view.post {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지
    }
}