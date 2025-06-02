package com.example.wooriga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter(private val events: MutableList<TimelineEvent>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    inner class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftCard: View = itemView.findViewById(R.id.leftCard)
        val rightCard: View = itemView.findViewById(R.id.rightCard)

        // 왼쪽
        val titleTextLeft: TextView = itemView.findViewById(R.id.titleTextLeft)
        val locationTextLeft: TextView = itemView.findViewById(R.id.locationTextLeft)
        val dateTextLeft: TextView = itemView.findViewById(R.id.dateTextLeft)

        // 오른쪽
        val titleTextRight: TextView = itemView.findViewById(R.id.titleTextRight)
        val locationTextRight: TextView = itemView.findViewById(R.id.locationTextRight)
        val dateTextRight: TextView = itemView.findViewById(R.id.dateTextRight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timeline_item, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val event = events[position]

        if (position % 2 == 0) {
            // 오른쪽 배치
            holder.leftCard.visibility = View.GONE
            holder.rightCard.visibility = View.VISIBLE

            holder.titleTextRight.text = event.title
            holder.locationTextRight.text = event.location
            holder.dateTextRight.text = event.dateString
        } else {
            // 왼쪽 배치
            holder.rightCard.visibility = View.GONE
            holder.leftCard.visibility = View.VISIBLE

            holder.titleTextLeft.text = event.title
            holder.locationTextLeft.text = event.location
            holder.dateTextLeft.text = event.dateString
        }
    }

    override fun getItemCount(): Int = events.size
}