package com.ghadeerh.customnavigationview

import android.graphics.drawable.Drawable

data class CustomMenuItem(
    var id: Int,
    var title: String = "",
    var icon: Drawable?,
    var isSelected: Boolean = false
)
