package com.example.wooriga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.model.Anniversary
import com.example.wooriga.utils.ToolbarUtils

class AnniversaryAdapter(
    private val context: Context, // 가족 그룹 별 색상 위해
    private var annivList: MutableList<Anniversary>,
    private val onItemClick: (Anniversary) -> Unit
) : RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconView: ImageView = itemView.findViewById(R.id.anniversaryListType)
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
        notifyDataSetChanged()  // 필터링 시만 사용
    }

    fun addToList(newItems: List<Anniversary>) {
        val start = annivList.size
        annivList.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size) // 페이징 시 사용
    }

    fun clearList() {
        annivList.clear()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anniversary_item, parent, false)
        /*view.elevation = 8f*/
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = annivList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anniv = annivList[position]
        holder.dateView.text = anniv.date
        holder.titleView.text = anniv.title
        holder.locationView.text = anniv.location

        // iconView 색상 적용
        val color = getColorByFamilyId(anniv.familyId)
        holder.iconView.setColorFilter(color)  // 색상 적용
    }

    private fun getColorByFamilyId(familyId: Int): Int {
        val index = ToolbarUtils.groupList.indexOfFirst { it.familyGroup.familyGroupId.toInt() == familyId }
        val colors = listOf(
            R.color.peach,
            R.color.mint,
            R.color.yellow,
            R.color.red,
            R.color.blue,
            R.color.purple,
            R.color.orange,
            R.color.brown,
        )
        return if (index != -1 && index < colors.size) {
            context.getColor(colors[index])
        } else {
            context.getColor(R.color.green)
        }
    }

}
