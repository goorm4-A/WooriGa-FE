package com.example.wooriga

import com.example.wooriga.model.History
import com.example.wooriga.model.HistoryRequest
import com.example.wooriga.model.HistoryTimeline

class HistoryRepository (private val apiService: ApiService) {

    // 가족사 등록
    suspend fun createEvent(historyRequest: HistoryRequest): Result<History> {
        return try {
            val response = apiService.createEvent(historyRequest)
            if (response.body()?.isSuccess == true) {
                response.body()?.result?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("결과 없음"))
            } else {
                Result.failure(Exception("등록 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 가족사 목록 조회
    suspend fun getEvents(familyId: Long): Result<List<History>> {
        return try {
            val response = apiService.getEvents(familyId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()?.result ?: emptyList())
            } else {
                val msg = response.body()?.message ?: "에러 발생"
                Result.failure(Exception("조회 실패: $msg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 지도에서 가족사 조회
    suspend fun getEventsMap(familyId: Long): Result<List<History>> {
        return try {
            val response = apiService.getEventsMap(familyId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()?.result ?: emptyList())
            } else {
                val msg = response.body()?.message ?: "에러 발생"
                Result.failure(Exception("조회 실패: $msg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}