package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemFamilyMoodBinding

class MoodAdapter(
    private val onItemClick: (Mood) -> Unit
) : ListAdapter<Mood, MoodAdapter.MoodViewHolder>(MOOD_COMPARATOR) {

    inner class MoodViewHolder(private val binding: ItemFamilyMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: Mood) {
            binding.textEmotion.text = mood.emotion
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
