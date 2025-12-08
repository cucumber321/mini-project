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
import com.example.bics.ui.user.EmailTextBox
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.viewmodel.ChangeEmailViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun ChangeEmailScreen(navController: NavController) {
    val changeEmailViewModel: ChangeEmailViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val available by changeEmailViewModel.available.collectAsState()
    val error by changeEmailViewModel.error.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ErrorCodeLaunchedEffect(error, context)

    fun onSubmit() {
        coroutineScope.launch {
            val entry = navController.currentBackStackEntry
            changeEmailViewModel.onSubmit({
                navController.navigate("${AppScreen.Success.name}/${R.string.change_email_success}") {
                    if (entry == navController.currentBackStackEntry) popUpTo(AppScreen.SecuritySettings.name) {inclusive = false}
                }
            })
        }
    }

    FormScreen(
        title = stringResource(R.string.change_email),
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp,
        fieldContent = {
            EmailTextBox(
                wrapper = changeEmailViewModel.email,
                enabled = available,
                title = R.string.new_email,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                }
            )
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.continue_text),
                onBottomButtonPressed = navController::navigateUp,
                bottomButtonText = stringResource(R.string.cancel),
                enabled = available
            )
        }
    )
}