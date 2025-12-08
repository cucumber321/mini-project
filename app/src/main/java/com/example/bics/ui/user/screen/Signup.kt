package com.example.bics.ui.user.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.example.bics.data.user.PasswordType
import com.example.bics.ui.user.EmailTextBox
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.PasswordTextBox
import com.example.bics.ui.user.UsernameTextBox
import com.example.bics.ui.user.viewmodel.SignupViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val signupViewModel: SignupViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val coroutineScope = rememberCoroutineScope()

    val error by signupViewModel.error.collectAsState()
    val available by signupViewModel.available.collectAsState()

    ErrorCodeLaunchedEffect(error, context)

    fun onSubmit() {
        coroutineScope.launch {
            val entry = navController.currentBackStackEntry
            signupViewModel.onSubmit({
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.signup_success),
                    Toast.LENGTH_SHORT
                ).show()
                if (entry == navController.currentBackStackEntry) navController.navigateUp()
            })
        }
    }

    FormScreen(
        title = stringResource(R.string.signup_text),
        fieldContent = {
            UsernameTextBox(
                wrapper = signupViewModel.usernameUiState,
                enabled = available,
                imeAction = ImeAction.Next
            )
            EmailTextBox(
                wrapper = signupViewModel.emailUiState,
                enabled = available,
                imeAction = ImeAction.Next
            )
            PasswordTextBox(
//Password
                wrapper = signupViewModel.passwordUiState,
                enabled = available,
                imeAction = ImeAction.Next,
            )
            PasswordTextBox(//Confirm Password
                wrapper = signupViewModel.confirmPasswordUiState,
                enabled = available,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                },
                passwordType = PasswordType.ConfirmPassword
            )
        },
        buttons = {
            Button(
                onClick = ::onSubmit,
                enabled = available
            ) {
                Text(stringResource(R.string.signup_text))
            }
        },
        modifier = Modifier.fillMaxSize(),
        fieldWeight = 2f,
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp,
        buttonArrangement = Arrangement.Center,
        enabled = available,
    )
}

