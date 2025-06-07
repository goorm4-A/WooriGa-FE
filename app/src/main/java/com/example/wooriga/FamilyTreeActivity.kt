package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityFamilyTreeBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class FamilyTreeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyTreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyTreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            dialog.dismiss() // 다이얼로그 닫기
            Toast.makeText(this, "가족이 추가되었습니다. $name, $relation, $birth", Toast.LENGTH_SHORT).show()

        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }

}