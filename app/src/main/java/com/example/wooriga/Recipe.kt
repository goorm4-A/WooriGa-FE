package com.example.wooriga

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val cookTimeMinutes: Int,
    val coverImageUrl: String?,
    val ingredients: List<Ingredient>,
    val steps: List<CookingStep>
) : Parcelable
