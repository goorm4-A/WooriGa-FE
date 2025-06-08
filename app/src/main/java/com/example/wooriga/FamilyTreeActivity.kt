package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityFamilyTreeBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class FamilyTreeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyTreeBinding
    private lateinit var familyTreeView: FamilyTreeView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyTreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        familyTreeView = binding.familyTreeView
        val container = binding.familyMemberContainer
        familyTreeView.setContainer(container) // FrameLayout 등
        familyTreeView.initializeTree(this)

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
        val name = bottomSheetBinding.nameInputF
        val spinner = bottomSheetBinding.relationSpinner
        val birth = bottomSheetBinding.birthInputF

        val cancelButton = bottomSheetBinding.cancelButtonF
        val submitButton = bottomSheetBinding.submitButtonF

        // 스피너 설정 (가족 관계)
        val relationOptions = resources.getStringArray(R.array.relation_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        submitButton.setOnClickListener {
            val name = name.text.toString()
            val relation = spinner.selectedItem.toString()
            val birth = birth.text.toString()


            if (name.isBlank() || birth.isBlank()) {
                Toast.makeText(this, "이름과 생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 여기에 가족 추가 로직 구현 (가계도 시각화)
            val member = FamilyMember(name, relation, birth, isUserAdded = true)
            familyTreeView.addMember(this, member)
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }

}