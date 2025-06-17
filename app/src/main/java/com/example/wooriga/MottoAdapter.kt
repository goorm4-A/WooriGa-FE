package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemFamilyMottoBinding

class MottoAdapter(private val onItemClick: (Motto) -> Unit) : ListAdapter<Motto, MottoAdapter.MottoViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Motto>() {
            override fun areItemsTheSame(oldItem: Motto, newItem: Motto): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Motto, newItem: Motto): Boolean = oldItem == newItem
        }
    }

    inner class MottoViewHolder(private val binding: ItemFamilyMottoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(motto: Motto) {
            binding.textMotto.text = motto.title
            binding.textDate.text = motto.createdAt

            // 아이템 클릭 시 상세 하단시트
            binding.root.setOnClickListener {
                onItemClick(motto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MottoViewHolder {
        val binding = ItemFamilyMottoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MottoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MottoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
