package com.example.wooriga

class MottoRepository {
    private val api = RetrofitClient.mottoApi

    suspend fun getMottos(familyId: Long, userId: Long, cursor: String? = null) =
        api.getMottos(familyId, userId, cursor)

    suspend fun addMotto(familyId: Long, mottoRequest: MottoRequest) =
        api.addMotto(familyId, mottoRequest)

    suspend fun deleteMotto(mottoId: Long, userId: Long) =
        api.deleteMotto(mottoId, userId)

    suspend fun updateMotto(familyId: Long, mottoId: Long, userId: Long, body: MottoRequest) =
        api.updateMotto(familyId, mottoId, userId, body)

}
