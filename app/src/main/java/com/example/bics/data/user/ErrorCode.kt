package com.example.bics.data.user

import androidx.annotation.StringRes
import com.example.bics.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

enum class ErrorCode(@StringRes val errorMessage: Int) {
    None(errorMessage = R.string.error_message_none),
    EmptyEmail(errorMessage = R.string.error_message_empty_email),
    EmptyPassword(errorMessage = R.string.error_message_empty_password),
    DifferentPassword(errorMessage = R.string.error_message_different_password),
    InvalidPassword(errorMessage = R.string.error_message_invalid_password),
    InvalidEmailFormat(errorMessage = R.string.error_message_invalid_email_format),
    InvalidCredentials(errorMessage = R.string.error_message_invalid_credentials),
    Unknown(errorMessage = R.string.error_message_unknown),
    EmptyUsername(errorMessage = R.string.error_message_empty_username),
    WeakPassword(errorMessage = R.string.error_message_weak_password),
    EmailInUse(errorMessage = R.string.error_message_email_in_use),
    NetworkError(errorMessage = R.string.error_message_network_error),
    UserDisabled(errorMessage = R.string.error_message_user_disabled),
    SameEmail(errorMessage = R.string.error_message_same_email),
    UnverifiedEmail(errorMessage = R.string.error_message_unverified_email),
    EmailChanged(errorMessage = R.string.error_message_email_changed),
    EmptyTitle(errorMessage = R.string.error_message_empty_title),
    EmptyDate(errorMessage = R.string.error_message_empty_date),
    InvalidTimeRange(errorMessage = R.string.error_message_invalid_time_range),
    NoUsers(errorMessage = R.string.error_message_no_users),
    EmptyField(errorMessage = R.string.error_message_empty_field),
    NoDispatchProducts(errorMessage = R.string.error_message_no_dispatch_products);

    companion object {

        fun processException(exception: Exception): ErrorCode {
            return when (exception) {
                is FirebaseAuthException -> {
                    when (exception.errorCode) {
                        "ERROR_INVALID_EMAIL" -> InvalidEmailFormat
                        "ERROR_INVALID_CREDENTIAL" -> InvalidCredentials
                        "ERROR_WEAK_PASSWORD" -> WeakPassword
                        "ERROR_EMAIL_ALREADY_IN_USE" -> EmailInUse
                        "ERROR_USER_DISABLED" -> UserDisabled
                        else -> Unknown
                    }
                }

                is FirebaseNetworkException -> NetworkError
                else -> Unknown
            }
        }
    }
}