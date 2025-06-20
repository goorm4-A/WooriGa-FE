package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wooriga.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


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

        // 이름
        binding.itemHomeUserprofile.userName.text = savedUser?.name ?: "이름 없음"
        /*
        // Test
        binding.itemHomeUserprofile.familyA.text = savedUser?.status ?: "상태 없음"
        binding.itemHomeUserprofile.familyB.text = savedUser?.birthDate ?: "b 없음"
        binding.itemHomeUserprofile.familyC.text = savedUser?.userId ?: "c 없음"
        */
        // 가족 그룹 관리 > 클릭
        binding.itemHomeFamily.buttonFamilyGroup.setOnClickListener {

            // 가족 그룹 관리 페이지로 이동
            val intent = Intent(requireContext(), ManageFamilyGroupActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}
