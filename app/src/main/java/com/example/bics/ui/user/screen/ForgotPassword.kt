package com.example.bics.ui.user.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.EmailTextBox
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.viewmodel.ForgotPasswordViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(factory = UserViewModelProvider.Factory)

    val error by forgotPasswordViewModel.error.collectAsState()
    val available by forgotPasswordViewModel.available.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    ErrorCodeLaunchedEffect(error, LocalContext.current)

    fun onSubmit() {
        coroutineScope.launch {
            val entry = navController.currentBackStackEntry
            forgotPasswordViewModel.onSubmit {
                navController.navigate("${AppScreen.Success.name}/${R.string.forgot_password_success}") {
                    if (entry == navController.currentBackStackEntry) popUpTo(AppScreen.ForgotPassword.name) {inclusive = true}
                }
            }
        }
    }

    FormScreen(
        title = stringResource(R.string.forgot_password),
        enabled = available,
        fieldContent = {
            if (forgotPasswordViewModel.user != null) {

                Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                    Text(text = forgotPasswordViewModel.user.email?:"", style = Typography.titleLarge)
                }
                Text(
                    text = stringResource(R.string.forgot_password_confirmation),
                    textAlign = TextAlign.Center
                )
            } else {
                EmailTextBox(
                    wrapper = forgotPasswordViewModel.emailUiState,
                    enabled = available,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        onSubmit()
                    }
                )
            }
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.continue_text),
                onBottomButtonPressed = { navController.navigateUp() },
                bottomButtonText = stringResource(R.string.cancel),
                enabled = available
            )
        },
        showBackButton = true,
        onBackButtonPressed = { navController.navigateUp() }
    )
}