package com.example.bics.ui.user.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.user.PasswordType
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.PasswordTextBox
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.viewmodel.ChangePasswordViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val changePasswordViewModel: ChangePasswordViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val focusManager = LocalFocusManager.current

    val error by changePasswordViewModel.error.collectAsState()
    val available by changePasswordViewModel.available.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    ErrorCodeLaunchedEffect(error, context)

    @SuppressLint("LocalContextResourcesRead")
    fun onSubmit() {
        coroutineScope.launch {
            val entry = navController.currentBackStackEntry
            changePasswordViewModel.onSubmit {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.change_password_success),
                    Toast.LENGTH_SHORT
                ).show()
                if (entry == navController.currentBackStackEntry) navController.navigateUp()
            }
        }
    }

    FormScreen(
        title = stringResource(R.string.change_password),
        fieldContent = {
            PasswordTextBox(
                wrapper = changePasswordViewModel.passwordUiState,
                enabled = available,
                passwordType = PasswordType.NewPassword,
                imeAction = ImeAction.Next
            )
            PasswordTextBox(
                wrapper = changePasswordViewModel.confirmPasswordUiState,
                enabled = available,
                passwordType = PasswordType.ConfirmPassword,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                }
            )
        },
        buttons = {
            ThickOptionButtons(
                enabled = available,
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.save),
                onBottomButtonPressed = navController::navigateUp,
                bottomButtonText = stringResource(R.string.cancel)
            )
        },
        enabled = available,
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp
    )
}