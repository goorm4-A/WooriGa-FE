package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityFamilyTreeBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class FamilyTreeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyTreeBinding
    private lateinit var familyTreeView: FamilyTreeView
    private val familyMembers = mutableListOf<FamilyMember>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyTreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        familyTreeView = binding.familyTreeView

        // "+" 버튼 클릭 -> 가족 추가 다이얼로그
        binding.addFamilyMemberButton.setOnClickListener {
            showAddFamilyMemberDialog()
        }

        // < 클릭 -> 이전 화면으로 이동
        binding.familyTreeToolbar.backButton.setOnClickListener {
            finish() // 이전 화면으로 이동
        }

    }

    private fun showAddFamilyMemberDialog() {
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddFamilyBinding.inflate(LayoutInflater.from(this))

        // 사진
        // val spinner = bottomSheetBinding.spinnerTagF
        val name = bottomSheetBinding.nameInputF
        val relation = bottomSheetBinding.relationInputF
        val birth = bottomSheetBinding.birthInputF

        val cancelButton = bottomSheetBinding.cancelButtonF
        val submitButton = bottomSheetBinding.submitButtonF

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        submitButton.setOnClickListener {
            // 여기에 가족 추가 로직 구현 (가계도)
            val name = name.text.toString()
            val relation = relation.text.toString()
            val birth = birth.text.toString()

            val member = FamilyMember(name, relation, birth)
            familyMembers.add(member)

            drawFamilyTree()

            dialog.dismiss() // 다이얼로그 닫기
            Toast.makeText(this, "가족이 추가되었습니다. $name, $relation, $birth", Toast.LENGTH_SHORT).show()

        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }

    private fun drawFamilyTree() {
        familyTreeView.clearAll()

        val genMap = mutableMapOf<Int, MutableList<FamilyMember>>()
        for (member in familyMembers) {
            val gen = getGenerationLevel(member.relation)
            genMap.getOrPut(gen) { mutableListOf() }.add(member)
        }

        genMap.toSortedMap().forEach { (gen, members) ->
            for (m in members) {
                val view = TextView(this).apply {
                    text = "${m.name}\n(${m.relation})"
                    setBackgroundResource(R.drawable.member_box_background)
                    textSize = 14f
                    setPadding(24, 24, 24, 24)
                }
                familyTreeView.addMemberView(view, gen)
            }
        }
    }

    private fun getGenerationLevel(relation: String): Int {
        return when (relation) {
            "할아버지", "할머니" -> 0
            "아버지", "어머니", "삼촌", "이모" -> 1
            "나", "형", "오빠", "누나", "언니", "동생" -> 2
            "아들", "딸" -> 4
            else -> 5
        }
    }


}