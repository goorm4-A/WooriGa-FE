package com.example.wooriga

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.BottomSheetAddAnniversaryBinding
import com.example.wooriga.databinding.BottomSheetDetailAnniversaryBinding
import com.example.wooriga.databinding.FragmentFamilyAnniversaryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.wooriga.model.Anniversary
import com.example.wooriga.utils.ToolbarUtils
import com.example.wooriga.utils.ToolbarUtils.currentGroup
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate

class FamilyAnniversaryFragment : Fragment() {

    private var _binding: FragmentFamilyAnniversaryBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: AnniversaryAdapter
    private val repository = AnniversaryRepository
    private val allAnnivList = mutableListOf<Anniversary>()
    private val loadedAnnivIds = mutableSetOf<Long>()

    private var selectedDate: String = ""
    var selected = currentGroup?.familyGroup

    // 페이징 관련 변수
    private var lastAnniversaryId: Long? = null
    private var hasNextPage: Boolean = true
    private var isLoading = false
    private var selectedTag: String? = null // null이면 전체 조회



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyAnniversaryBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        adapter = AnniversaryAdapter(requireContext(),allAnnivList, ::onAnnivItemClicked)
        binding.annivListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.annivListRecyclerView.adapter = adapter

        Log.d("Auth", "Token: ${UserManager.accessToken}")

        ToolbarUtils.restoreCurrentGroup(requireContext()) {
            selected = ToolbarUtils.currentGroup?.familyGroup
            Log.d("FamilyAnniv", "복원된 그룹: ${selected?.familyName}")
        }

        // 현재 선택된 가족 그룹
        if (selected == null) {
            Toast.makeText(requireContext(), "가족 그룹을 선택해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("FamilyAnniversaryFragment", "Selected group: ${selected!!.familyName}")
        }

        if (allAnnivList.isEmpty()) {
            loadInitialAnniversaries()
        }

        // 캘린더
        initCalendar()

        // 검색
        binding.customToolbar.iconSearch.setOnClickListener {
            // detail 액티비티로 이동
            startActivity(Intent(requireContext(), AnniversaryDetailActivity::class.java))
        }

        // 요일을 한글로 보이게 설정
        binding.calendarAnniv.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.anniv_weekdays)))

        // 상단바 가족 선택 아이콘 클릭 -> 가족 선택
        ToolbarUtils.setupFamilyGroupIcon(binding.customToolbar.iconSelectFamily, requireContext()) { selectedGroup ->
            selected = selectedGroup.familyGroup
            ToolbarUtils.saveCurrentGroup(requireContext(), selectedGroup)

        }

        // 리사이클러뷰 스크롤
        binding.annivListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && !isLoading && hasNextPage) {
                    lifecycleScope.launch {
                        loadMoreAnniversaries()
                    }
                }
            }
        })


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

            Log.d("AnnivDebug", "입력값22 - id: ${currentGroup?.familyGroup?.familyGroupId} title: $inputTitle, tag: $inputTag, location: $inputLocation, memo: $inputMemo")

            if (inputTitle.isEmpty() || inputTag == "태그") {
                Toast.makeText(requireContext(), "제목과 태그를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectedTag = inputTag

            if (annivToEdit != null) {
                // 수정: 기존 데이터 수정 후 리스트 갱신
                annivToEdit.title = inputTitle
                annivToEdit.tag = inputTag
                annivToEdit.location = inputLocation
                annivToEdit.memo = inputMemo

                adapter.updateList(allAnnivList)
                updateCalendarDecorators()
                filterRecyclerByMonth(binding.calendarAnniv.currentDate.year, binding.calendarAnniv.currentDate.month + 1)
                dialog.dismiss()
            } else {
                // 기념일 등록
                lifecycleScope.launch {
                    val newAnniv = selected?.let { it1 ->
                        Anniversary(
                            familyId = it1.familyGroupId.toInt(),
                            date = date,
                            title = inputTitle,
                            tag = inputTag,
                            location = inputLocation,
                            memo = inputMemo
                        )
                    }
                    Log.d("AnnivDebug", "생성된 기념일 객체: $newAnniv")

                    val success = newAnniv?.let { it1 ->
                        Log.d("AnnivDebug", "서버로 전송 시작: $it1")
                        repository.addToApi(it1)
                    }
                    Log.d("AnnivDebug", "서버 전송 결과 success=$success")

                    if (success == true) {
                        selectedTag = null  // 태그 초기화 (전체 보기)
                        loadInitialAnniversaries()
                        Toast.makeText(requireContext(), "등록 성공!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

/*                adapter.updateList(allAnnivList) // 추가 및 수정 후 반드시 갱신 호출
                dialog.dismiss()
                updateCalendarDecorators(binding.calendarAnniv.currentDate.month + 1) // 캘린더 업데이트
                // 현재 월 리스트 갱신
                filterRecyclerByMonth(
                    binding.calendarAnniv.currentDate.year,
                    binding.calendarAnniv.currentDate.month + 1
                )*/

            }
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
        bottomSheetBinding.closeButton.setOnClickListener { dialog.dismiss() }

        // 수정 버튼
        bottomSheetBinding.editButton.setOnClickListener {
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
            selectedDate = "%04d-%02d-%02d".format(date.year, date.month+1, date.day) // 선택된 날짜
            // 기념일 추가 다이얼로그
            showBottomSheetDialog(selectedDate, null)
        }
    }


/*    // CalendarDecorators.eventDecorator를 통해 달력에 점 추가
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
    }*/

    // RecyclerView 어댑터에 월 필터링 리스트만 업데이트
    private fun filterRecyclerByMonth(year: Int, month: Int) {
        val filtered = allAnnivList.filter {
            val parts = it.date.split("-").map(String::toInt)
            parts[0] == year && parts[1] == month
        }
        adapter.updateList(filtered)
    }

    // 추가 로딩
    private suspend fun loadMoreAnniversaries() {
        if (isLoading || !hasNextPage) return
        isLoading = true

        val pageable = mapOf(
            "page" to "0",
            "size" to "6",
            "sort" to "date,desc"
        )
        val result = AnniversaryRepository.fetchAnniversariesFromApi(
            type = selectedTag,
            lastId = lastAnniversaryId,
            pageable = pageable
        )

        if (result != null) {
            val newItems = result.contents.filter { it.anniversaryId != null && it.anniversaryId !in loadedAnnivIds }
            Log.d("AnnivDebug", "1 hasNextPage=$hasNextPage, lastAnniversaryId=$lastAnniversaryId, isLoading=$isLoading")

            if (newItems.isNotEmpty()) {
                allAnnivList.addAll(newItems)
                adapter.addToList(newItems)
                loadedAnnivIds.addAll(newItems.mapNotNull { it.anniversaryId })
                Log.d("AnnivDebug", "2 hasNextPage=$hasNextPage, lastAnniversaryId=$lastAnniversaryId, isLoading=$isLoading")

            }

            // ✅ 항상 갱신
            Log.d("AnnivDebug", "3 hasNextPage=$hasNextPage, lastAnniversaryId=$lastAnniversaryId, isLoading=$isLoading")

            lastAnniversaryId = result.contents.lastOrNull()?.anniversaryId?.toLong()
            hasNextPage = result.hasNext
            Log.d("AnnivDebug", "4 hasNextPage=$hasNextPage, lastAnniversaryId=$lastAnniversaryId, isLoading=$isLoading")

        } else {
            Toast.makeText(requireContext(), "기념일 로드 실패", Toast.LENGTH_SHORT).show()
        }

        isLoading = false
    }



    // 최초 목록 로딩
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadInitialAnniversaries() {
        lifecycleScope.launch {
            isLoading = true
            hasNextPage = true
            lastAnniversaryId = null
            loadedAnnivIds.clear()
            allAnnivList.clear()
            adapter.clearList()

            val pageable = mapOf(
                "page" to "0",
                "sort" to listOf("date,desc").joinToString(",") // 혹은 그냥 "date,desc"
            )
            val result = AnniversaryRepository.fetchAnniversariesFromApi(
                type = selectedTag,
                lastId = lastAnniversaryId,
                pageable = pageable
            )
            if (result != null) {
                Log.d("AnnivDebug", "result: ${result.contents}, hasNext: ${result.hasNext}, result: $result")
            }

            if (result != null) {
                val newItems = result.contents.filter { it.anniversaryId !in loadedAnnivIds }
                allAnnivList.addAll(newItems)
                adapter.updateList(newItems)
                loadedAnnivIds.addAll(newItems.mapNotNull { it.anniversaryId?.toLong() })
                lastAnniversaryId = newItems.lastOrNull()?.anniversaryId?.toLong()
                hasNextPage = result.hasNext

                updateCalendarDecorators(binding.calendarAnniv.currentDate.month + 1)
                filterRecyclerByMonth(binding.calendarAnniv.currentDate.year, binding.calendarAnniv.currentDate.month + 1)
            } else {
                Toast.makeText(requireContext(), "기념일을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    // 1. 색상 배열 (순서대로)
    private val colors = listOf(
        R.color.peach,
        R.color.mint,
        R.color.yellow,
        R.color.red,
        R.color.blue,
        R.color.purple,
        R.color.orange,
        R.color.brown,
    )

    // 2. 인덱스 기반 색상 반환 함수
    private fun getColorByFamilyId(familyId: Int): Int {
        val index = ToolbarUtils.groupList.indexOfFirst { it.familyGroup.familyGroupId.toInt() == familyId }
        return if (index != -1 && index < colors.size) {
            ContextCompat.getColor(requireContext(), colors[index])  // 이건 “color value”
        } else {
            ContextCompat.getColor(requireContext(), R.color.green)
        }
    }

    // 3. updateCalendarDecorators() 수정 예시
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendarDecorators(month: Int? = null) {
        val filteredAnnivs = allAnnivList.filter {
            it.date.substring(5,7).toInt() == (month ?: LocalDate.now().monthValue)
        }

        val dateColorMap = mutableMapOf<CalendarDay, Int>()

        filteredAnnivs.forEach { anniv ->
            val (y, m, d) = anniv.date.split("-").map { it.toInt() }
            val date = CalendarDay.from(y, m - 1, d)
            val color = getColorByFamilyId(anniv.familyId)
            dateColorMap[date] = color
        }

        val decorators = CalendarDecorators.buildEventDecorators(requireContext(), dateColorMap)
        binding.calendarAnniv.removeDecorators()
        decorators.forEach { binding.calendarAnniv.addDecorator(it) }

    //        binding.calendarAnniv.removeDecorators()
//        binding.calendarAnniv.addDecorator(CalendarDecorators.eventDecorator(requireContext(), dateColorMap))
    }




}


