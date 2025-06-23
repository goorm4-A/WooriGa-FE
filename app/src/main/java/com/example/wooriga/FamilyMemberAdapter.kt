package com.example.wooriga

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wooriga.databinding.ItemFamilyMemberBinding
import com.example.wooriga.model.FamilyMember

//
class FamilyMemberAdapter(
    private val onCheckedChanged: (Long, Boolean) -> Unit
) : ListAdapter<FamilyMember, FamilyMemberAdapter.ViewHolder>(diffCallback) {

    // ViewHolder 정의
    inner class ViewHolder(private val binding: ItemFamilyMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: FamilyMember) {
            binding.textName.text = member.familyMemberName ?: "이름 없음"

            // 프로필 이미지 로딩
            Glide.with(binding.root)
                .load(member.familyMemberImage)
                .circleCrop()
                .into(binding.imageProfile)

            // 체크 상태 반영 및 리스너 설정
            binding.checkbox.setOnCheckedChangeListener(null) // 리스너 초기화
            binding.checkbox.isChecked = selectedIds.contains(member.familyMemberId)

            binding.checkbox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    selectedIds.add(member.familyMemberId)
                } else {
                    selectedIds.remove(member.familyMemberId)
                }
                onCheckedChanged(member.familyMemberId, isChecked)
            }
        }
    }

    // 선택된 ID 목록
    private val selectedIds = mutableSetOf<Long>()

    fun getSelectedIds(): List<Long> = selectedIds.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFamilyMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FamilyMember>() {
            override fun areItemsTheSame(oldItem: FamilyMember, newItem: FamilyMember): Boolean {
                return oldItem.familyMemberId == newItem.familyMemberId
            }

            override fun areContentsTheSame(oldItem: FamilyMember, newItem: FamilyMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}

