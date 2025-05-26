package com.example.wooriga

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.ActivityManageFamilyGroupBinding

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
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족 그룹 B", 10))
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족 그룹 C", 8))
        adapter.notifyDataSetChanged()

        // "+" 버튼 클릭 시 다이얼로그 or 페이지 이동
        binding.createFamilyGroupButton.setOnClickListener {
        }
    }
}
