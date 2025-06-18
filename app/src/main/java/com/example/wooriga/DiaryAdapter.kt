package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.ItemDiaryCardBinding

class DiaryAdapter(
    private val onItemClick: (DiaryListItem) -> Unit
) : ListAdapter<DiaryListItem, DiaryAdapter.DiaryViewHolder>(DIFF_CALLBACK) {

    inner class DiaryViewHolder(private val binding: ItemDiaryCardBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryListItem) {
            binding.tvTitle.text = item.title
            Glide.with(binding.imageDiary)
                .load(item.imgUrl)
                .into(binding.imageDiary)

            binding.root.setOnClickListener { onItemClick(item) }
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiaryListItem>() {
            override fun areItemsTheSame(oldItem: DiaryListItem, newItem: DiaryListItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: DiaryListItem, newItem: DiaryListItem) = oldItem == newItem
        }
    }
}
