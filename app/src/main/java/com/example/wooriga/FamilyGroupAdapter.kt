package com.example.wooriga

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wooriga.databinding.ItemHomeGroupBinding

class FamilyGroupAdapter(private val groups: List<FamilyGroup>) :
    RecyclerView.Adapter<FamilyGroupAdapter.FamilyGroupViewHolder>() {

    inner class FamilyGroupViewHolder(val binding: ItemHomeGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyGroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeGroupBinding.inflate(inflater, parent, false)
        return FamilyGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FamilyGroupViewHolder, position: Int) {
        val group = groups[position]
        holder.binding.familyTitle.text = group.title
        holder.binding.familyCount.text = "${group.memberCount}명"
        // 이미지는 DB 연동 시 처리

        // family_group_container 클릭 -> 가족 트리 페이지로 이동
        holder.binding.familyGroupContainer.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, FamilyTreeActivity::class.java).apply {
                // 가족 그룹 id 추가해야함
                // putExtra("groupId", group.id)
                putExtra("title", group.title)
                putExtra("memberCount", group.memberCount)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = groups.size
}


