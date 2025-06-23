package com.example.wooriga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

sealed class RuleListItem {
    data class Header(val type: String) : RuleListItem()
    data class Content(val rule: Rule) : RuleListItem()
}

class RuleListAdapter(
    private val onItemClick: (Rule) -> Unit,
    private val onDeleteClick: (Rule) -> Unit
) : ListAdapter<RuleListItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CONTENT = 1

        val DiffCallback = object : DiffUtil.ItemCallback<RuleListItem>() {
            override fun areItemsTheSame(oldItem: RuleListItem, newItem: RuleListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: RuleListItem, newItem: RuleListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RuleListItem.Header -> VIEW_TYPE_HEADER
            is RuleListItem.Content -> VIEW_TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rule_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_family_motto, parent, false)
                ContentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is RuleListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is RuleListItem.Content -> (holder as ContentViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: RuleListItem.Header) {
            itemView.findViewById<TextView>(R.id.text_header).text = item.type
        }
    }

    inner class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: RuleListItem.Content) {
            itemView.findViewById<TextView>(R.id.text_motto).text = item.rule.title
            itemView.findViewById<TextView>(R.id.text_date).text = item.rule.date

            itemView.setOnClickListener { onItemClick(item.rule) }

            val context = itemView.context
            val btnMore = itemView.findViewById<View>(R.id.btn_more)
            btnMore.setOnClickListener {
                val popup = PopupMenu(context, btnMore)
                popup.menuInflater.inflate(R.menu.menu_motto_options, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            Toast.makeText(context, "수정 클릭됨", Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClick(item.rule)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}
