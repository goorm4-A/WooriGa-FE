package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemDiaryCardBinding

class DiaryAdapter(
    private val onItemClick: (Diary) -> Unit
) : ListAdapter<Diary, DiaryAdapter.DiaryViewHolder>(DIFF_CALLBACK) {

    inner class DiaryViewHolder(private val binding: ItemDiaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Diary) {
            binding.tvTitle.text = item.title
            // TODO: 사진 바인딩
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = ItemDiaryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.title == newItem.title && oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }
}
