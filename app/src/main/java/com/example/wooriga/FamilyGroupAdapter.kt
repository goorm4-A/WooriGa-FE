package com.example.wooriga

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.ItemHomeGroupBinding
import com.example.wooriga.model.FamilyGroupWrapper

// 
class FamilyGroupAdapter(private val groups: List<FamilyGroupWrapper>) :
    RecyclerView.Adapter<FamilyGroupAdapter.FamilyGroupViewHolder>() {

    inner class FamilyGroupViewHolder(val binding: ItemHomeGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyGroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeGroupBinding.inflate(inflater, parent, false)
        return FamilyGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FamilyGroupViewHolder, position: Int) {
        val wrapper = groups[position]
        val group = wrapper.familyGroup

        holder.binding.familyTitle.text = group.familyName
        holder.binding.familyCount.text = "${wrapper.totalCnt ?: 1}명"
        // 이미지 URL이 null 아니면 Glide로 이미지 로딩
        val imageUrl = group.familyImage
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_family)  // 로딩 중 표시할 이미지
                .error(R.drawable.ic_cross)              // 에러 시 표시할 이미지
                .into(holder.binding.familyImage)
        } else {
            // 이미지 URL 없으면 기본 이미지 표시
            holder.binding.familyImage.setImageResource(R.drawable.ic_family)
        }

        // family_group_container 클릭 -> 가족 트리 페이지로 이동
        holder.binding.familyGroupContainer.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, FamilyTreeActivity::class.java).apply {
                // 가족 그룹 id 추가해야함
                putExtra("groupId", group.familyGroupId)
            }
            context.startActivity(intent)
        }
        // + 버튼 누르면 초대 코드 보이도록
        holder.binding.addMemberButton.setOnClickListener() {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.invite_code_show_dialog, null)
            val inviteCodeText = dialogView.findViewById<TextView>(R.id.inviteCode)
            val okButton = dialogView.findViewById<Button>(R.id.submitButton)

            inviteCodeText.text = group.inviteCode.toString()

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            okButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int = groups.size

}


