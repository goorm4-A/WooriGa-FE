package com.example.wooriga

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.FragmentFamilyMottoBinding

class FamilyMottoFragment : Fragment() {

    private var _binding: FragmentFamilyMottoBinding? = null
    private val binding get() = _binding!!

    private lateinit var mottoAdapter: MottoAdapter
    private val viewModel: MottoViewModel by viewModels()

    private var familyId: Long = -1L
    val savedUser = UserManager.loadUserInfo()
    val userId = savedUser?.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전달받은 familyId 가져오기
        familyId = arguments?.getLong("familyId") ?: -1L
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

        // 확인용 로그
        Log.d("FamilyMottoFragment", "선택된 familyId: $familyId")

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
        mottoAdapter = MottoAdapter(
            // 가훈 아이템 클릭
            onItemClick = { clickedMotto ->
                MottoDetailBottomSheet(clickedMotto).show(parentFragmentManager, "MottoDetail")
            },
            // 삭제 버튼 클릭
            onDeleteClick = { motto ->
                userId?.let {
                    viewModel.deleteMotto(motto.id, it)
                } ?: Toast.makeText(context, "로그인 필요", Toast.LENGTH_SHORT).show()
            },
            // 수정 버튼 클릭
            onEditClick = { motto ->
                MottoEditBottomSheet(motto, viewModel).show(parentFragmentManager, "MottoEdit")
            }
        )

        binding.recyclerFamilyMotto.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFamilyMotto.adapter = mottoAdapter

        // LiveData 관찰
        viewModel.mottos.observe(viewLifecycleOwner) {
            Log.d("CHECK", "갱신된 가훈 수: ${it.size}")
            mottoAdapter.submitList(it)
        }

        // 데이터 불러오기
        if (userId != null) {
            viewModel.loadMottos(familyId = familyId, userId = userId)
        } else {
            Log.e("FamilyMottoFragment", "userId is null - 로그인 필요")
        }

        // 가훈 추가 버튼 클릭
        binding.addFamilyMotto.setOnClickListener {
            MottoAddBottomSheet { familyName, motto ->
                userId?.let {
                    viewModel.addMotto(userId = it, familyName = familyName, motto = motto)
                } ?: Toast.makeText(requireContext(), "로그인 필요", Toast.LENGTH_SHORT).show()
            }.show(parentFragmentManager, "AddMottoBottomSheet")
        }


    }

}