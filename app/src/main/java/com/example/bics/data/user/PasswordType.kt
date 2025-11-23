package com.example.bics.data.user

import androidx.annotation.StringRes
import com.example.bics.R

enum class PasswordType(
    @StringRes val upperLabel: Int,
    val isError: (ErrorCode) -> Boolean,
    val errorMessage: (ErrorCode) -> ErrorCode
) {
    Password(
        R.string.password,
        {
            it in listOf(
                ErrorCode.EmptyPassword,
                ErrorCode.DifferentPassword,
                ErrorCode.WeakPassword,
                ErrorCode.InvalidCredentials
            )
        },
        {
            if (
                it in listOf(
                    ErrorCode.EmptyPassword,
                    ErrorCode.WeakPassword,
                    ErrorCode.InvalidCredentials
                )
            ) it else ErrorCode.None
        }
    ),
    ConfirmPassword(
        R.string.confirm_password,
        {
            it == ErrorCode.DifferentPassword
        }, {
            if (it == ErrorCode.DifferentPassword) it else ErrorCode.None
        }
    ),
    CurrentPassword(
        R.string.current_password,
        {
          it in listOf(
              ErrorCode.InvalidCredentials,
              ErrorCode.EmptyPassword,
              ErrorCode.WeakPassword
          )
        },
        {
            when (it) {
                ErrorCode.EmptyPassword, ErrorCode.WeakPassword -> it
                ErrorCode.InvalidCredentials -> ErrorCode.InvalidPassword
                else -> ErrorCode.None
            }
        }
    ),
    NewPassword(
        R.string.new_password,
        {
            it in listOf(
                ErrorCode.EmptyPassword,
                ErrorCode.WeakPassword,
                ErrorCode.DifferentPassword,
            )
        },
        {
            if (it in listOf(
                    ErrorCode.EmptyPassword,
                    ErrorCode.WeakPassword,
                )) it else ErrorCode.None
        }
    )
}