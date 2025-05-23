package com.example.wooriga.ui.familydiary

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
            binding.textLabel.text = diary.preview

            // Glide로 이미지 로드
            Glide.with(binding.imageDiary.context)
                .load(diary.imageUrl)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.imageDiary)
        }
    }

    companion object {
        private val DIARY_COMPARATOR = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }
}
