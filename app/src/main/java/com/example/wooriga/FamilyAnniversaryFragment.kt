package com.example.wooriga

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.BottomSheetAddAnniversaryBinding
import com.example.wooriga.databinding.BottomSheetDetailAnniversaryBinding
import com.example.wooriga.databinding.FragmentFamilyAnniversaryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.wooriga.model.Anniversary
import com.example.wooriga.utils.ToolbarUtils
import com.example.wooriga.utils.ToolbarUtils.setupFamilyGroupIcon
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import java.time.LocalDate

class FamilyAnniversaryFragment : Fragment() {

    private var _binding: FragmentFamilyAnniversaryBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: AnniversaryAdapter
    private val repository = AnniversaryRepository
    private val allAnnivList = mutableListOf<Anniversary>()

    private var selectedDate: String = ""


    @RequiresApi(Build.VERSION_CODES.O)
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

        // 캘린더
        initCalendar()

        // 검색
        binding.customToolbar.iconSearch.setOnClickListener {
            // detail 액티비티로 이동
            val intent = Intent(requireContext(), AnniversaryDetailActivity::class.java)
            startActivity(intent)
        }

        // 요일을 한글로 보이게 설정
        binding.calendarAnniv.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.anniv_weekdays)))

        // 상단바 가족 선택 아이콘 클릭 -> 가족 선택
        val selectFamilyGroup = binding.customToolbar.iconSelectFamily
        selectFamilyGroup.setOnClickListener() {
            setupFamilyGroupIcon(it, requireContext(), ToolbarUtils.groupList) { selectedGroup ->
                // 선택된 가족 그룹에 대한 처리
                Toast.makeText(requireContext(), "${selectedGroup.title} 선택됨", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedUser = UserManager.loadUserInfo()
        // 사용자 이름을 가져와서 기념일 제목에 적용
        val name = savedUser?.name ?: "이름 없음"
        val message = "${name}님의 기념일들을\n관리해보세요!"

        val spannable = SpannableString(message)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.annivTitle.text = spannable

        // 오늘 날짜 기준으로 기념일들 필터링 후 리사이클러뷰 갱신
        val today = LocalDate.now()
        filterRecyclerByMonth(today.year, today.monthValue)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onAnnivItemClicked(anniv: Anniversary) {
        showAnniversaryDetailDialog(anniv)
    }


    // 기념일 등록 다이얼로그
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomSheetDialog(date: String, annivToEdit: Anniversary?) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetAddAnniversaryBinding.inflate(LayoutInflater.from(requireContext()))

        val titleInput = bottomSheetBinding.titleInput
        val spinner = bottomSheetBinding.tagInput
        val locationInput = bottomSheetBinding.locationInput
        val memoInput = bottomSheetBinding.memoInput
        val submitButton = bottomSheetBinding.submitButton
        val cancelButton = bottomSheetBinding.cancelButton
        val dateView = bottomSheetBinding.dateText

        dateView.text = date

        val types = listOf("태그") + resources.getStringArray(R.array.anniv_tag)
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
            updateCalendarDecorators(binding.calendarAnniv.currentDate.month+1) // 캘린더 업데이트
            // 현재 월 리스트 갱신
            filterRecyclerByMonth(
                binding.calendarAnniv.currentDate.year,
                binding.calendarAnniv.currentDate.month+1
            )

        }

        cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()
        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정

    }


    // 상세보기 다이얼로그
    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() = with(binding) {
        val calendar = calendarAnniv

        // 초기 데코레이터 설정
        updateCalendarDecorators()

        // 캘린더 달 이동 리스너
        calendar.setOnMonthChangedListener { _, date ->
            // 선택된 달의 월을 가져와서 데코레이터를 업데이트
            val month = date.month+1
            updateCalendarDecorators(month)
            filterRecyclerByMonth(date.year, month)
        }
        // 캘린더 날짜 선택 리스너 설정
        calendar.setOnDateChangedListener { _, date, _ ->

            // 선택된 날짜를 저장
            selectedDate = "%04d-%02d-%02d".format(date.year, date.month+1, date.day)
            // BottomSheetDialog를 통해 기념일 추가 화면을 띄운다.
            showBottomSheetDialog(selectedDate, null)
        }
    }


    // CalendarDecorators.eventDecorator를 통해 달력에 점 추가
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendarDecorators(month: Int? = null) {
        val eventSet = allAnnivList
            .filter { it.date.substring(5,7).toInt() == (month ?: LocalDate.now().monthValue) }
            .map { d ->
                val (y, m, day) = d.date.split("-").map(String::toInt)
                CalendarDay.from(y, m-1, day)
            }.toSet()

        binding.calendarAnniv.removeDecorators()
        binding.calendarAnniv.addDecorator(CalendarDecorators.eventDecorator(requireContext(), eventSet))
    }

    // RecyclerView 어댑터에 월 필터링 리스트만 업데이트
    private fun filterRecyclerByMonth(year: Int, month: Int) {
        val filtered = allAnnivList.filter {
            val parts = it.date.split("-").map(String::toInt)
            parts[0] == year && parts[1] == month
        }
        adapter.updateList(filtered)
    }


}


