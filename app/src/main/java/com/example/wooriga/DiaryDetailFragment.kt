package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.FragmentDiaryDetailBinding
import com.example.wooriga.databinding.ItemCommentBinding
import kotlinx.coroutines.launch
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.TimeZone

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    private val binding get() = _binding!!

    private val commentList = mutableListOf<DiaryComment>()

    // 일기 샘플
    val dummyDiaryDetail = DiaryDetailItem(
        diaryId = -1L,
        title = "테스트 일기입니다.",
        location = "강원도 평창",
        description = "따뜻한 날씨에 가족들과 캠핑을 다녀왔어요. 텐트도 치고 고기도 구워 먹었답니다.",
        contentType = "DIARY",
        diaryTags = listOf(
            DiaryTag(diaryTagId = 1L, diaryTagName = "캠핑"),
            DiaryTag(diaryTagId = 2L, diaryTagName = "가족")
        ),
        participantIds = listOf(101L, 102L),
        imgUrls = listOf("https://ik.imagekit.io/tvlk/blog/2024/10/shutterstock_2479862045.jpg?tr=q-70,c-at_max,w-500,h-250,dpr-2") // 샘플 이미지 URL
    )

    // diaryId만 받아서 API 호출
    private var diaryId: Long = -1L
    private lateinit var diary: DiaryDetailItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = requireArguments().getLong("diaryId", -1L)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val repository = DiaryRepository()

            // 일기 상세 데이터 불러오기
            val detail = repository.fetchDiaryDetail(diaryId)
            if (detail != null) {
                diary = detail
                bindDiary(detail)
            } else {
                Toast.makeText(requireContext(), "일기 상세 데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }

            // 댓글 조회
            val comments = repository.fetchDiaryComments(diaryId)
            if (comments != null) {
                commentList.clear()
                commentList.addAll(comments)
                renderComments()
            }
        }

        // 댓글 표시
        renderComments()

        // 댓글 전송 버튼 클릭
        binding.btnSend.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val repository = DiaryRepository()
                    val newComment = repository.postDiaryComment(
                        diaryId = diaryId,
                        memberId = 1L, // TODO: 로그인한 사용자 ID로 변경
                        content = text
                    )
                    if (newComment != null) {
                        commentList.add(newComment)
                        addCommentView(newComment)
                        binding.etComment.text?.clear()
                    } else {
                        Toast.makeText(requireContext(), "댓글 등록 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "추억"

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun bindDiary(diary: DiaryDetailItem) {
        binding.tvTitle.text = diary.title
        binding.tvLocation.text = diary.location
        binding.tvContent.text = diary.description
        binding.tvDate.text = "추후 추가" // TODO

        // 이미지
        if (diary.imgUrls.isNotEmpty()) {
            binding.ivPhoto.visibility = View.VISIBLE
            Glide.with(this)
                .load(diary.imgUrls.first())
                .into(binding.ivPhoto)
        } else {
            binding.ivPhoto.visibility = View.GONE
        }

        // 태그
        binding.containerTag.removeAllViews()
        diary.diaryTags.forEach { tag ->
            val tagView = layoutInflater.inflate(R.layout.item_diary_tag, binding.containerTag, false) as TextView
            tagView.text = tag.diaryTagName
            binding.containerTag.addView(tagView)
        }

        // 멘션
        binding.containerMention.removeAllViews()
        diary.participantIds.forEach { id ->
            val mentionView = layoutInflater.inflate(R.layout.item_diary_mention, binding.containerMention, false) as TextView
            mentionView.text = "@참여자$id"  // TODO
            binding.containerMention.addView(mentionView)
        }
    }

    private fun renderComments() {
        binding.containerCommentList.removeAllViews()
        for (comment in commentList) {
            addCommentView(comment) // DiaryComment 타입으로 호출
        }
    }

    // 댓글 조회
    private fun addCommentView(comment: DiaryComment) {
        val commentBinding = ItemCommentBinding.inflate(layoutInflater, binding.containerCommentList, false)

        commentBinding.tvUserName.text = comment.username
        commentBinding.tvContent.text = comment.content
        commentBinding.tvTime.text = formatDisplayTime(comment.createdAt)

        // TODO: 유저 프로필 이미지 받아오기
        commentBinding.ivUserImage.setImageResource(R.drawable.ic_user_default)

        binding.containerCommentList.addView(commentBinding.root)
    }

    fun formatDisplayTime(isoTime: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(isoTime)

            val formatter = SimpleDateFormat("M/dd HH:mm", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            "알 수 없음"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}