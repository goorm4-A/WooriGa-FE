package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wooriga.databinding.FragmentFamilyMottoBinding

class FamilyMottoFragment : Fragment() {

    private var _binding: FragmentFamilyMottoBinding? = null
    private val binding get() = _binding!!

    private lateinit var mottoAdapter: MottoAdapter
    private val viewModel: MottoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyMottoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val btnBack = toolbar.findViewById<ImageButton>(R.id.btn_back)

        title.text = "가훈"

        btnBack.setOnClickListener {
            requireActivity().findViewById<View>(R.id.bottomNavigation).visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // RecyclerView 세팅
        mottoAdapter = MottoAdapter()
        binding.recyclerFamilyMotto.adapter = mottoAdapter

        // LiveData 관찰
        viewModel.mottos.observe(viewLifecycleOwner) {
            mottoAdapter.submitList(it)
        }

        // 데이터 불러오기 (임시 id로 예시)
        viewModel.loadMottos(familyId = 1L, userId = 1L)

        // 추가 버튼 클릭
        binding.addFamilyMotto.setOnClickListener {
            AddMottoBottomSheet { familyName, motto ->
                viewModel.addMotto(userId = 1L, familyName = familyName, motto = motto)
            }.show(parentFragmentManager, "AddMottoBottomSheet")
        }


    }

}