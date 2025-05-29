package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.wooriga.databinding.FragmentFamilyCultureBinding

class FamilyCultureFragment : Fragment() {

    private var _binding: FragmentFamilyCultureBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyCultureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // include된 툴바에서 아이콘 가져오기
        val iconMenu = view.findViewById<Toolbar>(R.id.custom_toolbar)
            .findViewById<ImageView>(R.id.icon_menu)

        iconMenu.setOnClickListener {
            // 임시 토스트 메세지
            Toast.makeText(requireContext(), "가족 선택 클릭됨", Toast.LENGTH_SHORT).show()
    }
    }

}