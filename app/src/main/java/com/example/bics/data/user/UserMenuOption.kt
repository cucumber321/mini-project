package com.example.bics.data.user

import androidx.annotation.StringRes
import com.example.bics.data.AppScreen

data class UserMenuOption(
    @StringRes val text: Int,
    val navigateTo: AppScreen
)