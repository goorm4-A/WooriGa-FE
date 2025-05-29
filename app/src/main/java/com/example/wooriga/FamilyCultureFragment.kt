package com.example.wooriga

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wooriga.databinding.FragmentFamilyCultureBinding
import com.example.wooriga.databinding.FragmentHomeBinding

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

}