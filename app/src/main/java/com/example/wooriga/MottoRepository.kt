package com.example.wooriga

class MottoRepository {
    private val api = RetrofitClient.mottoApi

    suspend fun getMottos(familyId: Long, userId: Long, cursor: String? = null) =
        api.getMottos(familyId, userId, cursor)

    suspend fun addMotto(userId: Long, body: MottoRequest) =
        api.addMotto(userId, body)

    suspend fun deleteMotto(mottoId: Long, userId: Long) =
        api.deleteMotto(mottoId, userId)

    suspend fun updateMotto(mottoId: Long, userId: Long, body: MottoRequest) =
        api.updateMotto(mottoId, userId, body)
}
