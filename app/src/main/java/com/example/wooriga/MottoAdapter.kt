package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemFamilyMottoBinding

class MottoAdapter(
    private val onItemClick: (Motto) -> Unit,
    private val onDeleteClick: (Motto) -> Unit,
    private val onEditClick: (Motto) -> Unit
) : ListAdapter<Motto, MottoAdapter.MottoViewHolder>(DIFF_CALLBACK) {

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

            // 더보기 버튼 클릭
            binding.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.menu_motto_options, popup.menu)

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClick(motto)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(motto)
                            true
                        }
                        else -> false
                    }
                }

                popup.show()
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
