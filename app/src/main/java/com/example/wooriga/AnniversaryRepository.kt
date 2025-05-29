package com.example.wooriga

import com.example.wooriga.model.Anniversary

object AnniversaryRepository {

    private val anniversaryList = mutableListOf<Anniversary>()

    // 샘플 데이터 초기화
    fun loadSampleData() {
        anniversaryList.clear()
        anniversaryList.addAll(
            listOf(
                Anniversary("2025-06-01", "엄마 생신", "생일", "집", "미역국 끓이기"),
                Anniversary("2025-07-15", "친구 결혼식", "경조사", "웨딩홀", "축의금 챙기기"),
                Anniversary("2025-08-20", "치과 예약", "약속", "치과", "충치 치료"),
                Anniversary("2025-09-01", "기타 일정", "기타", "카페", "생각 정리")
            )
        )
    }

    // 태그 필터링된 리스트 반환 (tag가 ""이면 전체)
    fun getFiltered(tag: String): List<Anniversary> {
        return if (tag.isBlank()) {
            anniversaryList
        } else {
            anniversaryList.filter { it.tag == tag }
        }
    }

    // 전체 데이터 반환 (필요 시 사용 가능)
    fun getAll(): List<Anniversary> = anniversaryList

    // 항목 추가
    fun addAnniversary(anniv: Anniversary) {
        anniversaryList.add(anniv)
    }

/*    // 항목 수정
    fun updateAnniversary(anniv: Anniversary) {
        // 리스트에서 해당 기념일 찾아서 업데이트 (예: 날짜 + 제목으로 식별)
        val index =
            anniversaryList.indexOfFirst { it.date == anniv.date && it.title == anniv.title }
        if (index != -1) {
            anniversaryList[index] = anniv
        }
    }

    // 항목 삭제
    fun deleteAnniversary(anniv: Anniversary) {
        anniversaryList.removeIf { it.date == anniv.date && it.title == anniv.title }
    }*/
}