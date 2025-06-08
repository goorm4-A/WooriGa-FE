package com.example.wooriga

data class FamilyMember(
    val name: String,
    val relation: String,
    val birth: String,
    var x: Float = 0f,
    var y: Float = 0f,
    val isUserAdded: Boolean = false,
    var father: FamilyMember? = null,
    var mother: FamilyMember? = null
)