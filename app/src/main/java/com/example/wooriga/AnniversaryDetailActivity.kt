package com.example.wooriga

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wooriga.databinding.ActivityAnniversaryDetailBinding
import com.example.wooriga.model.Anniversary

class AnniversaryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnniversaryDetailBinding

    private lateinit var adapter: AnniversaryAdapter
    private val repository = AnniversaryRepository  // Repository 사용
    private var filteredAnnivList = mutableListOf<Anniversary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnniversaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initData()
        initFilterButtons()

        // 뒤로가기 (이전 화면으로 이동)
        binding.annivDetailToolbar.buttonBack.setOnClickListener {
            finish()
        }

        // 검색 기능 추가
        binding.annivSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterByTitle(it)  // 입력된 검색어로 필터링
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterByTitle(it)  // 입력된 검색어로 필터링
                }
                return true
            }
        })

    }

    private fun initRecyclerView() {
        adapter = AnniversaryAdapter(mutableListOf()) { anniv ->
            // 아이템 클릭 시 필요한 동작 구현
        }
        binding.annivListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.annivListRecyclerView.adapter = adapter

        // 중첩스크롤 활성화
        binding.annivListRecyclerView.isNestedScrollingEnabled = false
    }

    private fun initData() {
        repository.getAll()  // 샘플 데이터 불러오기
        filterByTag("") // 전체 보기

    }

    private fun initFilterButtons() {
        binding.annivTypeEvent.setOnClickListener { filterByTag("경조사") }
        binding.annivTypeBirth.setOnClickListener { filterByTag("기념일") }
        binding.annivTypeAppoint.setOnClickListener { filterByTag("모임/약속") }
        binding.annivTypeEtc.setOnClickListener { filterByTag("기타") }
        binding.annivTypeAll.setOnClickListener { filterByTag("") }
    }

    // 태그 클릭
    private fun filterByTag(tag: String) {
        filteredAnnivList = repository.getFiltered(tag).toMutableList()
        adapter.updateList(filteredAnnivList)
    }

    // 검색
    private fun filterByTitle(query: String) {
        val allList = repository.getAll()
        filteredAnnivList = if (query.isBlank()) {
            allList.toMutableList()
        } else {
            allList.filter {
                it.title.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        adapter.updateList(filteredAnnivList)
    }


}

