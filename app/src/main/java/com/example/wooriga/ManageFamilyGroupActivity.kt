package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.ActivityManageFamilyGroupBinding
import com.example.wooriga.databinding.BottomSheetAddFamilyGroupBinding
import com.example.wooriga.model.FamilyGroupResponse
import com.example.wooriga.model.FamilyGroupWrapper
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
    private val groupList = mutableListOf<FamilyGroupWrapper>()

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

        // 서버에서 데이터 불러오기
        fetchFamilyGroupsFromServer()


        // 뒤로가기 버튼 클릭 -> 이전 화면으로 이동
        binding.manageFamilyGroupToolbar.backButton.setOnClickListener {
            finish()
        }

        // "+ 그룹 만들기" 버튼 클릭 시 다이얼로그 or 페이지 이동
        binding.createFamilyGroupButton.setOnClickListener {
            showFamilyGroupBottomSheetDialog()
        }

        // "+ 초대 코드 입력하기" 버튼 클릭 시 다이얼로그
        binding.addInviteCode.setOnClickListener() {
            val inviteDialogView =
                LayoutInflater.from(this).inflate(R.layout.invite_code_dialog, null)
            val inviteDialog = AlertDialog.Builder(this)
                .setView(inviteDialogView)
                .create()

            inviteDialogView.findViewById<Button>(R.id.submitButton).setOnClickListener {
                val code =
                    inviteDialogView.findViewById<EditText>(R.id.inviteCodeInput).text.toString()
                // TODO: 서버에 초대코드로 그룹 참여 요청

                inviteDialog.dismiss() // 다이얼로그 닫기
            }
            inviteDialogView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
                inviteDialog.dismiss()
            }

            inviteDialog.show()
        }
        // 그룹 아이템 클릭 -> 가족 트리 페이지로 이동

    }


    override fun onResume() {
        super.onResume()
        fetchFamilyGroupsFromServer()
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

                // 서버에 저장
                val (nameBody, imagePart) = prepareRequest(groupName, selectedImageFile)

                RetrofitClient2.familyGroupApi.createGroup(nameBody, imagePart).enqueue(object :
                    Callback<ApiResponse<FamilyGroupResponse>> {
                    override fun onResponse(
                        call: Call<ApiResponse<FamilyGroupResponse>>,
                        response: Response<ApiResponse<FamilyGroupResponse>>
                    ) {
                        if (response.isSuccessful) { // 응답이 성공적일 때
                            val body = response.body()
                            val result = body?.result
                            if (result != null) {  // 그룹 리스트에 추가
                                val wrapper = FamilyGroupWrapper(result, null, null)
                                groupList.add(wrapper)
                                adapter.notifyItemInserted(groupList.size - 1)
                                dialog.dismiss()

                            } else {
                                Log.e("GroupCreate", "결과가 null입니다")
                            }
                        } else {
                            Log.e("GroupCreate", "서버 오류: ${response.code()} ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<FamilyGroupResponse>>, t: Throwable) {
                        Log.e("GroupCreate", "네트워크 오류", t)
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

    // 서버에서 데이터 불러오기
    private fun fetchFamilyGroupsFromServer() {
        RetrofitClient2.familyGroupApi.getGroups().enqueue(object : Callback<ApiResponse<List<FamilyGroupWrapper>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<FamilyGroupWrapper>>>,
                response: Response<ApiResponse<List<FamilyGroupWrapper>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val resultList = body?.result

                    if (resultList != null) {
                        groupList.clear()
                        groupList.addAll(resultList)
                        adapter.notifyDataSetChanged()
                        Log.d("FetchGroups", "가족 그룹 ${resultList.size}개 로드 성공")
                    } else {
                        Log.e("FetchGroups", "result가 null입니다. message=${body?.message}")
                    }
                } else {
                    Log.e("FetchGroups", "HTTP 오류: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<FamilyGroupWrapper>>>, t: Throwable) {
                Log.e("FetchGroups", "네트워크 오류", t)
            }
        })
    }




}
