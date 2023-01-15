package com.ghadeerh.customnavigationview

import android.graphics.drawable.Drawable

object MenuConfigurations {

    var itemSelected: Int? = null

    var menuBackgroundColor: Int = 0

    var iconTintColor: Int = 0
    var iconBackgroundTintColor: Int = 0
    var textColor: Int = 0
    var selectedIconTintColor: Int = 0
    var selectedIconBackgroundTintColor: Int = 0
    var selectedTextColor: Int = 0

    var toggleButtonIcon: Drawable? = null
    var toggleButtonTintColor: Int = 0
    var toggleButtonBackgroundTintColor: Int = 0

    var gridSpanCount: Int = 0
    var gridBoxRadius: Float = 0.0f

    var onMenuItemClickListener: OnMenuItemClickListener? = null
    var collapseOnClickOutside: Boolean = true

    var maxExpandWidth: Int = 0
    var minExpandWidth: Int = 0

}