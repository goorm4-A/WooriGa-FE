package com.example.wooriga

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.BottomSheetAddHistoryBinding
import com.example.wooriga.databinding.FragmentFamilyHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.wooriga.model.HistoryRequest
import com.example.wooriga.utils.ToolbarUtils
import com.example.wooriga.utils.ToolbarUtils.currentGroup

class FamilyHistoryFragment : Fragment() {

    private lateinit var timelineRecyclerView: RecyclerView
    private lateinit var adapter: TimelineAdapter

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(HistoryRepository(RetrofitClient2.apiService))
    }

    private var _binding: FragmentFamilyHistoryBinding? = null
    private val binding get() = _binding!!

    private var _bottomSheetBinding: BottomSheetAddHistoryBinding? = null
    private val bottomSheetBinding get() = _bottomSheetBinding!!

    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private var selectedAddress: String = "" // 선택된 주소 저장용
    private var selectedLocalDate: LocalDate? = null  // 선택된 날짜 저장용
    private var dialogView: View? = null

    private var selected = currentGroup?.familyGroup


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFamilyHistoryBinding.inflate(inflater, container, false)

        timelineRecyclerView = binding.timelineRecyclerView
        adapter = TimelineAdapter(emptyList())
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
        ToolbarUtils.setupFamilyGroupIcon(binding.toolbarHistory.iconSelectFamily, requireContext()) { selectedGroup ->
            // 선택된 가족 그룹에 대한 처리
            selected = selectedGroup.familyGroup
            ToolbarUtils.saveCurrentGroup(requireContext(), selectedGroup)
            binding.toolbarHistory.currentGroup.text = selected!!.familyName

            // 가족이 선택되면 ViewModel을 통해 해당 가족의 이벤트 불러오기
            selected?.familyGroupId?.let { familyId ->
                viewModel.getEvents(familyId)
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

        // 가족 그룹 정보
        ToolbarUtils.restoreCurrentGroup(requireContext()) {
            selected = ToolbarUtils.currentGroup?.familyGroup
            selected?.let {
                binding.toolbarHistory.currentGroup.text = it.familyName
                viewModel.getEvents(it.familyGroupId) // ← 이걸 여기서 확실하게 호출
            } ?: run {
                Toast.makeText(requireContext(), "가족 그룹을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }



        // 선택된 가족 그룹 있을 때
        viewModel.historyList.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        // 초기 로딩 시 현재 가족의 이벤트 불러오기
        selected?.familyGroupId?.let { familyId ->
            viewModel.getEvents(familyId)
        }

    }

    override fun onResume() {
        super.onResume()
        selected?.familyGroupId?.let {
            viewModel.getEvents(it)
        }
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

        val cancelButton = bottomSheetBinding.cancelButton
        val addButton = bottomSheetBinding.submitButton


        // 날짜 선택
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedLocalDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formattedDate = selectedLocalDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
            val familyName = selected?.familyName ?: "기본 가족"


            if (title.isNotBlank() && selectedAddress.isNotBlank() && selectedLocalDate != null) {
                val request = HistoryRequest(
                    family = familyName,
                    dateString = dateString,
                    title = title,
                    locationName = selectedAddress,
                    latitude = selectedLatitude,
                    longitude = selectedLongitude

                )
                Log.d("map", "add request : $selectedLatitude, $selectedLongitude")

                viewModel.createEvent(
                    request = request,
                    onSuccess = {
                        Toast.makeText(requireContext(), "가족사 등록 완료!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                )

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


    //지도 위치 선택 관련
    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            selectedAddress = data?.getStringExtra("address") ?: ""
            selectedLatitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            selectedLongitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

            Log.d("map", " request : $selectedLatitude, $selectedLongitude")

            // 선택된 주소를 다이얼로그에 반영
            bottomSheetBinding.locationOutput.text = selectedAddress
        }
    }

}
