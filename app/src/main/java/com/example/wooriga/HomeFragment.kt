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

        binding.itemHomeFamily.buttonFamilyGroup.setOnClickListener {

            // 가족 그룹 관리 페이지로 이동
            val intent = Intent(requireContext(), ManageFamilyGroupActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}
