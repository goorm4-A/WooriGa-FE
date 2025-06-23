package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
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

        fetchFamilyGroupsFromServer()

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

        // 프로필 사진
        val profileUrl = savedUser?.image
        Log.e("HomeFragment", "Profile URL: $profileUrl")

        // 카카오 프로필이 기본이라면 앱 기본 프로필 사용
        val isDefaultProfile = profileUrl.isNullOrBlank() || profileUrl.contains("default_profile.jpeg")

        if (isDefaultProfile) {
            // 기본 이미지면 없는 걸로 처리 → 앱 기본 프로필 사용
            binding.itemHomeUserprofile.userProfile.setImageResource(R.drawable.ic_profile)
        } else {
            // 사용자가 설정한 카카오 프로필 사진
            Glide.with(requireContext())
                .load(profileUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.itemHomeUserprofile.userProfile)
        }

        // 오늘의 소식
        fetchTodayImages()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchFamilyGroupsFromServer()
    }

    override fun onPause() {
        super.onPause()
        stopImageSwitcher()
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

                        // 이미지 있는 그룹 필터링
                        val imageGroups = groupList.filter {
                            !it.familyGroup.familyImage.isNullOrEmpty()
                        }

                        // 이미지 목록 생성
                        familyImages.clear()
                        familyImages.addAll(imageGroups.mapNotNull { it.familyGroup.familyImage })

                        if (familyImages.isNotEmpty()) {
                            currentImageIndex = 0
                            startImageSwitcher()
                        } else {
                            stopImageSwitcher()
                            // 기본 이미지 표시
                            binding.itemHomeFamily.imageFamilyGroup.setImageResource(R.drawable.ic_family)
                        }

                        val familyGroupName = groupList.mapNotNull { it.familyGroup.familyName }.distinct()
                        val familyTextViews = listOf(
                            binding.itemHomeUserprofile.family1,
                            binding.itemHomeUserprofile.family2,
                            binding.itemHomeUserprofile.family3,
                            binding.itemHomeUserprofile.family4
                        )
                        familyTextViews.forEachIndexed { index, textView ->
                            if (index < familyGroupName.size) {
                                textView.text = "#${familyGroupName[index]}"
                                textView.visibility = View.VISIBLE
                            } else {
                                textView.visibility = View.GONE
                            }
                        }

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

    private val familyImages = mutableListOf<String>()
    private var currentImageIndex = 0
    private var imageSwitcherRunnable: Runnable? = null
    private val imageSwitchInterval = 4000L // 4초

    // 이미지 전환을 위한 핸들러 설정
    private val imageSwitchHandler = android.os.Handler()

    private fun startImageSwitcher() {
        if (familyImages.isEmpty() || imageSwitcherRunnable != null) return

        imageSwitcherRunnable = object : Runnable {
            override fun run() {
                val imageUrl = familyImages[currentImageIndex]
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_family)
                    .error(R.drawable.ic_cross)
                    .into(binding.itemHomeFamily.imageFamilyGroup)

                // 다음 인덱스 준비
                currentImageIndex = (currentImageIndex + 1) % familyImages.size

                // 다음 호출 예약
                imageSwitchHandler.postDelayed(this, imageSwitchInterval)
            }
        }

        imageSwitchHandler.post(imageSwitcherRunnable!!)
    }

    private fun stopImageSwitcher() {
        imageSwitcherRunnable?.let {
            imageSwitchHandler.removeCallbacks(it)
            imageSwitcherRunnable = null // 초기화
        }
    }

    private fun fetchTodayImages() {
        RetrofitClient2.todayImagesApi.getTodayImages().enqueue(object : Callback<TodayImagesResponse> {
            override fun onResponse(
                call: Call<TodayImagesResponse>,
                response: Response<TodayImagesResponse>
            ) {
                if (response.isSuccessful) {
                    val todayImages = response.body()?.result?.todayImages.orEmpty()

                    val imageViews = listOf(
                        binding.itemHomeNews.imageNews1,
                        binding.itemHomeNews.imageNews2,
                        binding.itemHomeNews.imageNews3,
                        binding.itemHomeNews.imageNews4
                    )

                    // 최대 4개만 표시
                    for (i in imageViews.indices) {
                        if (i < todayImages.size) {
                            imageViews[i].visibility = View.VISIBLE
                            Glide.with(requireContext())
                                .load(todayImages[i])
                                .placeholder(R.drawable.rounded_background_gray)
                                .error(R.drawable.ic_cross)
                                .centerCrop()
                                .into(imageViews[i])
                        } else {
                            imageViews[i].visibility = View.GONE
                        }
                    }
                    Log.d("TodayImages", "imageView[0] is null? ${binding.itemHomeNews.imageNews1 == null}")


                } else {
                    Log.e("TodayImages", "서버 응답 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TodayImagesResponse>, t: Throwable) {
                Log.e("TodayImages", "네트워크 오류", t)
            }
        })
    }

    override fun onDestroyView() {
        stopImageSwitcher()
        super.onDestroyView()
        _binding = null
    }


}
