package com.example.wooriga

data class Rule(
    val id: Long,
    val family: String,
    val type: String,
    val title: String,
    val description: String,
    val date: String
)

data class RuleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RuleResult?
)

data class RuleResult(
    val rules: List<RuleDto>,
    val hasNext: Boolean,
    val nextCursor: String?
)

data class RuleDto(
    val id: Long,
    val title: String,
    val familyName: String,
    val ruleType: String, // REQUIRED / RECOMMENDED / PROHIBITED
    val createdAt: String,
    val description: String? = null
)

data class RuleRequest(
    val familyName: String,
    val ruleType: String,
    val title: String,
    val description: String
)

data class RuleDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RuleDto?
)
