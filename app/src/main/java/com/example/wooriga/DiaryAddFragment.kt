package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wooriga.databinding.FragmentDiaryAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryAddFragment : Fragment() {

    private var _binding: FragmentDiaryAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 설정
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val btnClose = toolbar.findViewById<ImageButton>(R.id.btn_close)
        val btnDone = toolbar.findViewById<TextView>(R.id.btn_done)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)

        title.text = "일기 등록"

        // 닫기 버튼
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 완료 버튼
        btnDone.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val location = binding.etLocation.text.toString()
            val content = binding.etContent.text.toString()
            val tagText = binding.etTag.text.toString()
            val memberText = binding.etMember.text.toString()
            val date = binding.tvDate.text.toString()

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(requireContext(), "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tagList = tagText.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            val memberList = memberText.split(',').map { it.trim() }.filter { it.isNotEmpty() }

            val diary = Diary(
                date = date,
                imageUri = null, // TODO: 이미지 URI 적용 시 처리
                title = title,
                location = location,
                content = content,
                tag = tagList,
                member = memberList
            )

            viewModel.addDiary(diary)
            parentFragmentManager.popBackStack()
        }

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("M월 d일 E요일", Locale.KOREAN)
        binding.tvDate.text = formatter.format(calendar.time)

        binding.buttonAddImage.setOnClickListener {
            // TODO: 이미지 선택 로직
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
