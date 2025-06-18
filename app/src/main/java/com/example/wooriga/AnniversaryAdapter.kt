package com.example.wooriga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.model.Anniversary

class AnniversaryAdapter(
    private var annivList: MutableList<Anniversary>,
    private val onItemClick: (Anniversary) -> Unit
) : RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.anniversaryListDate)
        val titleView: TextView = itemView.findViewById(R.id.anniversaryListTitle)
        val locationView: TextView = itemView.findViewById(R.id.anniversaryListLocation)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(annivList[position])
                }
            }
        }
    }

    fun updateList(newList: List<Anniversary>) {
        annivList = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anniversary_item, parent, false)
        view.elevation = 8f
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = annivList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anniv = annivList[position]
        holder.dateView.text = anniv.date
        holder.titleView.text = anniv.title
        holder.locationView.text = anniv.location
    }
}