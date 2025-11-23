package com.example.bics.ui.user.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.ui.user.CenteredOptionButtons
import com.example.bics.ui.user.EmailTextBox
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.PasswordTextBox
import com.example.bics.ui.user.viewmodel.LoginViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val error by loginViewModel.error.collectAsState()
    val available by loginViewModel.available.collectAsState()

    ErrorCodeLaunchedEffect(error, context)

    fun onSubmit() {
        coroutineScope.launch {
            loginViewModel.onSubmit {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    FormScreen(
        title = stringResource(R.string.login_title),
        fieldContent = {
            EmailTextBox(
                wrapper = loginViewModel.emailUiState,
                enabled = available,
                imeAction = ImeAction.Next,
            )
            PasswordTextBox(
                wrapper = loginViewModel.passwordUiState,
                enabled = available,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                },
                includeForgotPassword = true,
                onForgotPasswordButtonPressed = { navController.navigate(AppScreen.ForgotPassword.name) {launchSingleTop = true} }
            )
        },
        buttons = {
            CenteredOptionButtons(
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.login_text),
                onBottomButtonPressed = { navController.navigate(AppScreen.Signup.name) {launchSingleTop = true} },
                bottomButtonText = stringResource(R.string.signup_text),
                enabled = available,
            )
        },
        modifier = Modifier.fillMaxSize(),
        buttonArrangement = Arrangement.Center,
        enabled = available,
    )


}



