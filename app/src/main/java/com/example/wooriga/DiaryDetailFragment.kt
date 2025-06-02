package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.FragmentDiaryDetailBinding
import com.example.wooriga.databinding.ItemCommentBinding

// data 클래스 정의
data class Comment(
    val userName: String,
    val content: String,
    val date: String,
    val profileImageUrl: String? = null
)

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    private val binding get() = _binding!!

    // 댓글 리스트 (샘플)
    private val commentList = mutableListOf(
        Comment("김숙명", "우와 재밌었겠다", "2025.04.12", null),
        Comment("이유진", "나도 가보고 싶어!", "2025.04.13", null)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 댓글 표시
        renderComments()

        // 전송 버튼 클릭
        binding.btnSend.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                val newComment = Comment("나", text, "2025.04.27", null)
                commentList.add(newComment)
                addCommentView(newComment)
                binding.etComment.text?.clear()
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

    private fun renderComments() {
        binding.containerCommentList.removeAllViews()
        for (comment in commentList) {
            addCommentView(comment)
        }
    }

    private fun addCommentView(comment: Comment) {
        val commentBinding = ItemCommentBinding.inflate(layoutInflater, binding.containerCommentList, false)

        commentBinding.tvCommentUser.text = comment.userName
        commentBinding.tvCommentText.text = comment.content
        commentBinding.tvCommentDate.text = comment.date

        if (comment.profileImageUrl != null) {
            Glide.with(this)
                .load(comment.profileImageUrl)
                .circleCrop()
                .into(commentBinding.ivCommentUserImage)
        } else {
            commentBinding.ivCommentUserImage.setImageResource(R.drawable.ic_user_circle)
        }

        binding.containerCommentList.addView(commentBinding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}