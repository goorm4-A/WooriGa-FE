package com.example.wooriga

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CookingStep(
    val stepNumber: Int,
    val description: String,
    val imageUrl: String? = null
) : Parcelable

