package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wooriga.databinding.FragmentHomeBinding
import com.example.wooriga.model.FamilyGroupWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val groupList = mutableListOf<FamilyGroupWrapper>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 일단 home_notice 누르면 로그아웃 되도록
        binding.customToolbar.homeNotice.setOnClickListener {
            // 로그아웃 처리
            UserManager.logout()

            // 로그인 화면으로 이동
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }
        val savedUser = UserManager.loadUserInfo()


        // 가족 그룹 관리 > 클릭
        binding.itemHomeFamily.buttonFamilyGroup.setOnClickListener {

            // 가족 그룹 관리 페이지로 이동
            val intent = Intent(requireContext(), ManageFamilyGroupActivity::class.java)
            startActivity(intent)
        }


        // 이름
        binding.itemHomeUserprofile.userName.text = savedUser?.name ?: "이름 없음"

        // 프로필 가족 그룹 태그
        val familyTextViews = listOf(
            binding.itemHomeUserprofile.family1,
            binding.itemHomeUserprofile.family2,
            binding.itemHomeUserprofile.family3,
            binding.itemHomeUserprofile.family4
        )

        val familyGroupName = groupList.mapNotNull { it.familyGroup.familyName }.distinct()
        familyTextViews.forEachIndexed { index, textView ->
            if (index < familyGroupName.size) {
                textView.text = "#${familyGroupName[index]}"
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchFamilyGroupsFromServer()
    }

    // 가족 그룹 리스트를 서버에서 받아오는 함수
    fun fetchFamilyGroupsFromServer(onComplete: () -> Unit = {}) {
        RetrofitClient2.familyGroupApi.getGroups().enqueue(object :
            Callback<ApiResponse<List<FamilyGroupWrapper>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<FamilyGroupWrapper>>>,
                response: Response<ApiResponse<List<FamilyGroupWrapper>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        groupList.clear()
                        groupList.addAll(body.result)
                        onComplete() // 데이터 받아온 후 콜백 호출
                    } else {
                        Log.e("ToolbarUtils", "서버 응답 실패")
                    }
                } else {
                    Log.e("ToolbarUtils", "서버 오류")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<FamilyGroupWrapper>>>, t: Throwable) {
                Log.e("ToolbarUtils", "네트워크 오류", t)
            }
        })
    }
}
