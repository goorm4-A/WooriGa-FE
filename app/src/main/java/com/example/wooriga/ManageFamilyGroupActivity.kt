package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.ActivityManageFamilyGroupBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyGroupBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

data class FamilyGroup(
    val imageResId: Int,
    val title: String,
    val memberCount: Int
)

class ManageFamilyGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageFamilyGroupBinding
    private lateinit var adapter: FamilyGroupAdapter
    private val groupList = mutableListOf<FamilyGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageFamilyGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        adapter = FamilyGroupAdapter(groupList)
        binding.familyGroupRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.familyGroupRecyclerView.adapter = adapter

        // 테스트
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족 그룹 A", 4))
        adapter.notifyDataSetChanged()

        // 뒤로가기 버튼 클릭 -> 이전 화면으로 이동
        binding.manageFamilyGroupToolbar.backButton.setOnClickListener {
            finish()
        }

        // "+ 그룹 만들기" 버튼 클릭 시 다이얼로그 or 페이지 이동
        binding.createFamilyGroupButton.setOnClickListener {
            showFamilyGroupBottomSheetDialog()
        }

        // 그룹 아이템 클릭 -> 가족 트리 페이지로 이동

    }

    private fun showFamilyGroupBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddFamilyGroupBinding.inflate(LayoutInflater.from(this))

        // 이미지 추가
        val name = bottomSheetBinding.nameInput
        val cancelButton = bottomSheetBinding.cancelButton
        val submitButton = bottomSheetBinding.submitButton

        cancelButton.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        submitButton.setOnClickListener {
            val groupName = name.text.toString()
            if (groupName.isNotEmpty()) {
                // 그룹 추가 로직 (DB 연동 등)
                groupList.add(FamilyGroup(R.drawable.ic_family, groupName, 1)) // 임시로 0명
                adapter.notifyDataSetChanged()
                dialog.dismiss() // 다이얼로그 닫기
            } else {
                name.error = "그룹 이름을 입력해주세요."
            }
        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정

    }

}
