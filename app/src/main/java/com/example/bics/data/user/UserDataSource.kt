package com.example.bics.data.user

import com.example.bics.R
import androidx.core.net.toUri
import com.example.bics.data.AppScreen

object UserDataSource {

    const val DEFAULT_USERNAME = ""
    const val CONTACT_PHONE_NUMBER = "+60 11-1444 3997"
    const val CONTACT_EMAIL = "support@inventorycontrolsystem-ffd8f.firebaseapp.com"
    val defaultProfilePicture =
        "https://firebasestorage.googleapis.com/v0/b/inventorycontrolsystem-ffd8f.firebasestorage.app/o/profile_picture_default.png?alt=media&token=d88d3781-3e3e-4942-9053-c4aa453effae".toUri()

    val userMenuOptions: List<UserMenuOption> = listOf(
        UserMenuOption(R.string.edit_profile, AppScreen.EditProfile),
        UserMenuOption(R.string.security_settings, AppScreen.ConfirmPassword),
        UserMenuOption(R.string.contact_us, AppScreen.Contact),
    )
    val securitySettingsOptions: List<UserMenuOption> = listOf(
        UserMenuOption(R.string.change_email, AppScreen.ChangeEmail),
        UserMenuOption(R.string.change_password, AppScreen.ChangePassword),
    )

}


