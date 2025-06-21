package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.ActivityManageFamilyGroupBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyGroupBinding
import com.example.wooriga.model.FamilyGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

const val REQUEST_CODE_IMAGE_PICK = 1001

class ManageFamilyGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageFamilyGroupBinding
    private lateinit var adapter: FamilyGroupAdapter
    private val groupList = mutableListOf<FamilyGroup>()

    private lateinit var dialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: BottomSheetAddFamilyGroupBinding


    private var selectedImageFile: File? = null // 선택된 이미지 파일을 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageFamilyGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // RecyclerView 설정
        adapter = FamilyGroupAdapter(groupList)
        binding.familyGroupRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.familyGroupRecyclerView.adapter = adapter

        // 테스트
        //groupList.add(FamilyGroup(R.drawable.ic_family, "가족 그룹 A", 4))


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

        dialog = BottomSheetDialog(this)
        bottomSheetBinding = BottomSheetAddFamilyGroupBinding.inflate(layoutInflater)


        // 이미지 추가
        val imageButton = bottomSheetBinding.buttonAddImageFG
        val name = bottomSheetBinding.nameInput
        val cancelButton = bottomSheetBinding.cancelButton
        val submitButton = bottomSheetBinding.submitButton

        imageButton.setOnClickListener {
            // 이미지 선택 로직 (예: 갤러리 열기)
            // 현재는 임시로 null 설정
            // 실제 구현 시 이미지 선택 후 File 객체 생성 필요
            //val imageFile: File? = selectImageFromGallery()
            //imageButton.setImageURI(imageUri) // 선택한 이미지로 변경
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        submitButton.setOnClickListener {
            val groupName = name.text.toString()
            if (groupName.isNotEmpty()) {
                val (nameBody, imagePart) = prepareRequest(groupName, selectedImageFile)

                RetrofitClient2.familyGroupApi.createGroup(nameBody, imagePart).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("GroupCreate", "그룹 생성 성공!")
                            groupList.add(FamilyGroup(null, null, groupName, 1))
                            adapter.notifyDataSetChanged()
                            dialog.dismiss()
                        } else {
                            Log.e("GroupCreate", "실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("GroupCreate", "에러 발생", t)
                    }
                })

            } else {
                name.error = "그룹 이름을 입력해주세요."
            }
        }

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        dialog.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫히도록 설정

    }


    // 이미지 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                // 서버 전송을 위해 파일로 변환
                val file = FileUtil.from(this, it)
                selectedImageFile = file // 전역 변수에 저장
                // 이미지뷰 등에 미리보기 표시
                bottomSheetBinding.imagePreviewFG.setImageURI(uri)
            }
        }
    }

    // image, name 요청 형식에 맞게 준비
    fun prepareRequest(name: String, imageFile: File?): Pair<RequestBody, MultipartBody.Part?> {
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        return Pair(nameBody, imagePart)
    }


}
