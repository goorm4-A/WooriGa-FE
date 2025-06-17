package com.example.wooriga

data class Mood(
    val id: Long,
    val family: String,
    val emotion: String,
    val tags: List<String>
)
