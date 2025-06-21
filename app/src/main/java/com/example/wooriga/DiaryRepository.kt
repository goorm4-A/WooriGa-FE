package com.example.wooriga

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.wooriga.RetrofitClient.diaryApi
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DiaryRepository {
    private val api = RetrofitClient.diaryApi

    // 일기 목록 조회
    suspend fun fetchDiaryList(
        page: Int,
        size: Int,
        familyId: Long,
        sort: List<String> = listOf("createdAt,desc"),
        lastDiaryId: Long? = null
    ): List<DiaryListItem>? {
        val response = api.getDiaryList(page, size, sort, familyId, lastDiaryId)
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result?.contents
        } else null
    }

    // 일기 상세 조회
    suspend fun fetchDiaryDetail(diaryId: Long): DiaryDetailItem? {
        val response = api.getDiaryDetail(diaryId)
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result
        } else null
    }

    // 댓글 조회
    suspend fun fetchDiaryComments(diaryId: Long): List<DiaryComment>? {
        val response = api.getDiaryComments(
            diaryId = diaryId,
            page = 0,
            size = 20
        )
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result?.comments
        } else null
    }

    // 댓글 작성
    suspend fun postDiaryComment(
        diaryId: Long,
        memberId: Long,
        content: String
    ): DiaryComment? {
        val request = CommentPostRequest(
            content = content,
            createdAt = getCurrentTimeIso()
        )

        val response = api.postDiaryComment(diaryId, memberId, request)
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result
        } else null
    }

    fun getCurrentTimeIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    // 댓글 삭제
    suspend fun deleteComment(commentId: Long): CommonResponse? {
        return try {
            val response = api.deleteComment(commentId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    // 대댓글 조회
    suspend fun fetchReComments(parentCommentId: Long, page: Int = 0, size: Int = 10): List<DiaryComment>? {
        return try {
            val response = api.getReComments(
                commentId = parentCommentId,
                page = page,
                size = size
            )
            if (response.isSuccessful) {
                response.body()?.result?.comments
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // 일기 등록
    suspend fun uploadDiary(
        dto: FamilyDiaryDto,
        imageUri: Uri?,
        context: Context
    ): Boolean {
        val gson = Gson()
        val json = gson.toJson(dto)
        Log.d("DiaryUpload", "요청 DTO: $json")

        val dtoBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val imagePart = imageUri?.let {
            val file = it.toFile(context)
            val requestFile = file.asRequestBody("image/*".toMediaType())
            val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            Log.d("DiaryUpload", "이미지 파일명: ${file.name}")
            multipart
        }

        try {
            val response = api.postFamilyDiary(dtoBody, imagePart?.let { listOf(it) })

            Log.d("DiaryUpload", "isSuccessful: ${response.isSuccessful}")
            Log.d("DiaryUpload", "response.code: ${response.code()}")
            Log.d("DiaryUpload", "response.message: ${response.message()}")
            Log.d("DiaryUpload", "response.body: ${response.body()?.message}")
            Log.d("DiaryUpload", "response.errorBody: ${response.errorBody()?.string()}")

            return response.isSuccessful && response.body()?.isSuccess == true
        } catch (e: Exception) {
            Log.e("DiaryUpload", "예외 발생: ${e.message}", e)
            return false
        }
    }


    fun Uri.toFile(context: Context): File {
        val inputStream = context.contentResolver.openInputStream(this)!!
        val file = File.createTempFile("upload", ".jpg", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    // 일기 삭제
    suspend fun deleteDiary(diaryId: Long): CommonResponse? {
        return try {
            val response = diaryApi.deleteDiary(diaryId)
            if (response.isSuccessful) {
                response.body()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // 일기 검색
    suspend fun searchDiaryList(
        familyId: Long,
        keyword: String,
        page: Int = 0,
        size: Int = 20,
        sort: List<String> = listOf("createdAt,desc"),
        lastDiaryId: Long? = null
    ): List<DiaryListItem>? {
        val response = api.searchDiaries(familyId, keyword, lastDiaryId, page, size, sort)
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result?.contents
        } else null
    }


}

