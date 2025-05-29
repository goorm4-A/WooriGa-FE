package com.example.wooriga
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.ItemDiaryCardBinding
import com.example.wooriga.model.Diary

class DiaryAdapter : ListAdapter<Diary, DiaryAdapter.DiaryViewHolder>(DIARY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = ItemDiaryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiaryViewHolder(private val binding: ItemDiaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) {
            binding.textTitle.text = diary.title

            // Glide로 이미지 로드
            Glide.with(binding.imageDiary.context)
                .load(diary.imageUri)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.imageDiary)

            // 이미지: 이미지 URI가 null이 아니면 표시
//            if (diary.imageUri != null) {
//                binding.imageDiary.setImageURI(Uri.parse(diary.imageUri))
//            } else {
//                binding.imageDiary.setImageResource(R.drawable.placeholder_image) // 기본 이미지
//            }

            // TODO: 사용자 이미지 로드
        }
    }

    companion object {
        private val DIARY_COMPARATOR = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }
}
