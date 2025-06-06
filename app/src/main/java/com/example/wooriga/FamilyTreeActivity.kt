package com.example.wooriga

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wooriga.databinding.ActivityFamilyTreeBinding
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
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme) // 스타일 적용
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_family, null)

        // 사진
        val spinner = view.findViewById<android.widget.Spinner>(R.id.spinnerTagF)
        val name = view.findViewById<EditText>(R.id.nameInputF)
        val relation = view.findViewById<EditText>(R.id.relationInputF)
        val birth = view.findViewById<EditText>(R.id.birthInputF)

        val cancelButton = view.findViewById<android.widget.Button>(R.id.cancelButtonF)
        val submitButton = view.findViewById<android.widget.Button>(R.id.submitButtonF)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        submitButton.setOnClickListener {
            // 여기에 가족 추가 로직 구현 (가계도)
            dialog.dismiss() // 다이얼로그 닫기
            Toast.makeText(this, "가족이 추가되었습니다. $name, $relation, $birth", Toast.LENGTH_SHORT).show()

        }

        dialog.setContentView(view)
        view.post {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정
    }

}