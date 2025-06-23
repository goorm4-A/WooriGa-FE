package com.example.wooriga

class RuleRepository {
    private val api = RetrofitClient.ruleApi

    suspend fun getRules(familyId: Long, userId: Long) =
        api.getRules(familyId, userId)

    suspend fun addRule(request: RuleRequest) =
        api.addRule(request)
}
