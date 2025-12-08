package com.example.bics.ui.user.screen

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.data.user.PasswordType
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.PasswordTextBox
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.viewmodel.ConfirmPasswordViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun ConfirmPasswordScreen(navController: NavController) {
    val confirmPasswordViewModel: ConfirmPasswordViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val focusManager = LocalFocusManager.current

    val error by confirmPasswordViewModel.error.collectAsState()
    val available by confirmPasswordViewModel.available.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    ErrorCodeLaunchedEffect(error, LocalContext.current)


    fun onSubmit() {
        coroutineScope.launch {
            confirmPasswordViewModel.onSubmit({
                navController.navigate(AppScreen.SecuritySettings.name) {popUpTo(AppScreen.ConfirmPassword.name) { inclusive = true }}
            })
        }
    }

    FormScreen(
        title = stringResource(R.string.confirm_password_title),
        fieldContent = {
            PasswordTextBox(
                wrapper = confirmPasswordViewModel.passwordUiState,
                enabled = available,
                includeForgotPassword = true,
                onForgotPasswordButtonPressed = { navController.navigate(AppScreen.ForgotPassword.name) },
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                },
                passwordType = PasswordType.CurrentPassword
            )
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.continue_text),
                onBottomButtonPressed = {
                    navController.navigateUp()
                },
                bottomButtonText = stringResource(R.string.cancel),
                enabled = available
            )
        },
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp,
        enabled = available,
    )
}