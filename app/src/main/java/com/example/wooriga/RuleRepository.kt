package com.example.wooriga

class RuleRepository {
    private val api = RetrofitClient.ruleApi

    suspend fun getRules(familyId: Long, userId: Long) =
        api.getRules(familyId, userId)

    suspend fun addRule(request: RuleRequest) =
        api.addRule(request)

    suspend fun getRuleDetail(ruleId: Long) =
        api.getRuleDetail(ruleId)

    suspend fun deleteRule(ruleId: Long) =
        api.deleteRule(ruleId)

    suspend fun updateRule(ruleId: Long, request: RuleRequest): CommonResponse2<RuleResponse> {
        return api.updateRule(ruleId, request)
    }

}
