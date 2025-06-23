package com.example.wooriga

object MoodTypeMapper {

    private val toKorean = mapOf(
        "EMOTION" to "감정",
        "TRAIT" to "성격",
        "VALUE" to "가치관"
    )

    private val toEnum = toKorean.entries.associate { (k, v) -> v to k }

    fun toKorean(enum: String): String = toKorean[enum] ?: enum
    fun toEnum(korean: String): String = toEnum[korean] ?: korean

}