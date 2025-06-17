package com.example.wooriga

data class FamilyMember(
    var name: String,
    var relation: String,
    var birth: String,
    var x: Float = 0f,
    var y: Float = 0f,
    val isUserAdded: Boolean = false,
    var father: FamilyMember? = null,
    var mother: FamilyMember? = null
)