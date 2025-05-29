package com.example.wooriga.model

data class Diary(
    val date: String,
    val imageUri: String?,
    val title: String,
    val tag: String, // TODO: List<String>,
    val member: String, // TODO: List<String>,
    val location: String,
    val content: String
)
