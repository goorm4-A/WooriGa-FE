package com.example.wooriga

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.BottomSheetAddHistoryBinding
import com.example.wooriga.databinding.FragmentFamilyHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.wooriga.model.History
import com.example.wooriga.utils.ToolbarUtils
import com.example.wooriga.utils.ToolbarUtils.setupFamilyGroupIcon

class FamilyHistoryFragment : Fragment() {

    private lateinit var timelineRecyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter
    private val events = mutableListOf<History>()

    private var _binding: FragmentFamilyHistoryBinding? = null
    private val binding get() = _binding!!

    private var _bottomSheetBinding: BottomSheetAddHistoryBinding? = null
    private val bottomSheetBinding get() = _bottomSheetBinding!!

    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private var selectedAddress: String = "" // 선택된 주소 저장용
    private var selectedLocalDate: LocalDate? = null  // 선택된 날짜 저장용
    private var dialogView: View? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFamilyHistoryBinding.inflate(inflater, container, false)

        timelineRecyclerView = binding.timelineRecyclerView
        adapter = TimelineAdapter(events)
        timelineRecyclerView.adapter = adapter
        timelineRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // "+" 버튼 클릭 -> 가족사 추가 (다이얼로그)
        val addHistoryButton = binding.addFamilyHistoryButton
        addHistoryButton.setOnClickListener {
            showHistoryBottomSheetDialog()
        }

        // 상단바 지도 아이콘 클릭 -> 지도 액티비티로 이동
        val mapButton = binding.toolbarHistory.iconMap
        mapButton.setOnClickListener {
            val intent = Intent(requireContext(), HistoryMapsActivity::class.java)
            startActivity(intent)
        }

        // 상단바 가족 선택 아이콘 클릭 -> 가족 선택
        val selectFamilyGroup = binding.toolbarHistory.iconSelectFamily
        selectFamilyGroup.setOnClickListener() {
            setupFamilyGroupIcon(it, requireContext(), ToolbarUtils.groupList) { selectedGroup ->
                // 선택된 가족 그룹에 대한 처리
                Toast.makeText(requireContext(), "${selectedGroup.title} 선택됨", Toast.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val savedUser = UserManager.loadUserInfo()
        // 사용자 이름을 가져와서 가족사 제목에 적용
        val name = savedUser?.name ?: "이름 없음"
        val message = "${name}님의 가족사들을\n관리해보세요!"

        val spannable = SpannableString(message)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.historyTitle.text = spannable
    }

    // 타임라인 항목 추가 함수
    private fun addTimelineEvent(event: History) {
        events.add(event)
        sortEventsByDate()
        adapter.notifyDataSetChanged()
        timelineRecyclerView.scrollToPosition(events.size - 1)
    }

    // 가족사 등록 다이얼로그
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showHistoryBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        _bottomSheetBinding = BottomSheetAddHistoryBinding.inflate(LayoutInflater.from(requireContext()))
        dialogView = view // 위치 선택 후 주소 반영 위해 저장

        val dateInput = bottomSheetBinding.dateInput
        val dateOutput = bottomSheetBinding.dateOutput
        val titleInput = bottomSheetBinding.titleInput
        val locationInput = bottomSheetBinding.locationInput
        // val locationOutput = bottomSheetBinding.locationOutput

        val cancelButton = bottomSheetBinding.cancelButton
        val addButton = bottomSheetBinding.submitButton


        // 날짜 선택
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedLocalDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formattedDate = selectedLocalDate!!.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    dateOutput.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        // 위치 선택
        locationInput.setOnClickListener {
            val intent = Intent(requireContext(), LocationPickerActivity::class.java)
            locationPickerLauncher.launch(intent)
        }

        // 추가 버튼
        addButton.setOnClickListener {
            val title = titleInput.text.toString()
            val dateString = dateOutput.text.toString()

            if (title.isNotBlank() && selectedAddress.isNotBlank() && selectedLocalDate != null) {
                val event = History(
                    family = "A 가족", // 임시, 실제로는 선택된 가족 이름으로 변경 필요
                    dateString = dateString,
                    dateObject = selectedLocalDate!!,
                    title = title,
                    locationName = selectedAddress,
                    latitude = selectedLatitude,
                    longitude = selectedLongitude

                )
                addTimelineEvent(event) // 추가된 가족사 전달


                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()
        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    
    // 날짜 별로 정렬
    private fun sortEventsByDate() {
        events.sortWith(compareBy { it.dateObject })
        adapter.notifyDataSetChanged()
    }

    //지도 위치 선택 관련
    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            selectedAddress = data?.getStringExtra("address") ?: ""
            selectedLatitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            selectedLongitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            // 선택된 주소를 다이얼로그에 반영
            bottomSheetBinding.locationOutput.text = selectedAddress
        }
    }





}
