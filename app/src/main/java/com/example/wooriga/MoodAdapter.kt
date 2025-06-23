package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemFamilyMoodBinding

class MoodAdapter(
    private val onItemClick: (Mood) -> Unit,
    private val onDeleteClick: (Mood) -> Unit
) : ListAdapter<Mood, MoodAdapter.MoodViewHolder>(MOOD_COMPARATOR) {

    inner class MoodViewHolder(private val binding: ItemFamilyMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: Mood) {
            binding.textEmotion.text = MoodTypeMapper.toKorean(mood.moodType)

            // 태그 렌더링 (FlexboxLayout에 동적으로 추가)
            binding.layoutTags.removeAllViews()
            mood.tags.forEach { tag ->
                val tagView = TextView(binding.root.context).apply {
                    text = "#$tag"
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    background = ContextCompat.getDrawable(context, R.drawable.bg_tag)
                    setPadding(24, 8, 24, 8)
                    textSize = 12f
                }
                binding.layoutTags.addView(tagView)
            }

            // 더보기 버튼 팝업 처리
            binding.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.menu_mood_options, popup.menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_delete -> {
                            onDeleteClick(mood)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }

            binding.root.setOnClickListener {
                onItemClick(mood)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemFamilyMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val MOOD_COMPARATOR = object : DiffUtil.ItemCallback<Mood>() {
            override fun areItemsTheSame(oldItem: Mood, newItem: Mood): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Mood, newItem: Mood): Boolean {
                return oldItem == newItem
            }
        }
    }
}
