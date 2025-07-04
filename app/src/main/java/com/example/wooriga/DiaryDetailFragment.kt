package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.FragmentDiaryDetailBinding
import com.example.wooriga.databinding.ItemCommentBinding
import com.example.wooriga.databinding.ItemCommentReBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    private val binding get() = _binding!!

    private val commentList = mutableListOf<DiaryComment>()

    // 일기 샘플
    val dummyDiaryDetail = DiaryDetailItem(
        username = "손예림",
        profile = "",
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

        // 하단 네비게이션 숨기기
        requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.GONE

        if (diaryId == -1L) {
            // 더미 데이터 직접 바인딩
            diary = dummyDiaryDetail
            bindDiary(diary)
            renderComments()  // 샘플 댓글 없으면 비워짐
            return
        }

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

        // 툴바
        val toolbar = view?.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar?.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar?.findViewById<ImageButton>(R.id.btn_back)
        val btnMore = toolbar?.findViewById<ImageButton>(R.id.btn_more)

        title?.text = "추억"

        btnBack?.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnMore?.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)

            // TODO: 로그인한 사용자 ID (예: 현재 로그인한 사용자 ID)
            val currentUserId = 1L

            if (diary.participantIds.contains(currentUserId)) {
                // 본인 글일 경우
                popupMenu.menu.add("수정")
                popupMenu.menu.add("삭제")
            } else {
                // 타인 글일 경우
                popupMenu.menu.add("신고")
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "수정" -> {
                        Toast.makeText(requireContext(), "수정 클릭됨", Toast.LENGTH_SHORT).show()
                        true
                    }
                    "삭제" -> {
                        lifecycleScope.launch {
                            val repository = DiaryRepository()
                            val response = repository.deleteDiary(diaryId)
                            if (response?.isSuccess == true) {
                                Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            } else {
                                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    "신고" -> {
                        Toast.makeText(requireContext(), "신고 클릭됨", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
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

        // 댓글 삭제
        // TODO: 현재 로그인한 사용자 ID
        val currentUserId = 1L

        commentBinding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)

            if (comment.familyMemberId == currentUserId) {
                popupMenu.menu.add("삭제")
            } else {
                popupMenu.menu.add("신고")
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "삭제" -> {
                        lifecycleScope.launch {
                            val result = DiaryRepository().deleteComment(comment.commentId)
                            if (result?.isSuccess == true) {
                                Toast.makeText(context, "댓글 삭제 완료", Toast.LENGTH_SHORT).show()
                                // UI에서 삭제
                                binding.containerCommentList.removeView(commentBinding.root)
                            } else {
                                Toast.makeText(context, "댓글 삭제 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    "신고" -> {
                        Toast.makeText(context, "댓글 신고 클릭됨", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        binding.containerCommentList.addView(commentBinding.root)
    }

    // 대댓글 조회
    private fun loadAndRenderReComments(parentCommentId: Long, parentContainer: LinearLayout) {
        lifecycleScope.launch {
            val reComments = DiaryRepository().fetchReComments(parentCommentId)
            if (reComments != null) {
                for (comment in reComments) {
                    val reBinding = ItemCommentReBinding.inflate(layoutInflater, parentContainer, false)

                    reBinding.tvName.text = comment.username
                    reBinding.tvContent.text = comment.content
                    reBinding.tvTime.text = formatDisplayTime(comment.createdAt)
                    reBinding.ivUserImage.setImageResource(R.drawable.ic_user_default)

                    // TODO: 대댓글 더보기 버튼 처리

                    parentContainer.addView(reBinding.root)
                }
            }
        }
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