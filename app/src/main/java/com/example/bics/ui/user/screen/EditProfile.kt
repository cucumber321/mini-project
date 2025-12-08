package com.example.bics.ui.user.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ProfilePicture
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.UsernameTextBox
import com.example.bics.ui.user.viewmodel.EditProfileViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val editProfileViewModel: EditProfileViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val chooseImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = editProfileViewModel::onImageChanged)
    val focusManager = LocalFocusManager.current

    val error by editProfileViewModel.error.collectAsState()
    val available by editProfileViewModel.available.collectAsState()
    val selectedImageUri by editProfileViewModel.selectedImageUri.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    ErrorCodeLaunchedEffect(error, context)

    fun onSubmit() {
        val entry = navController.currentBackStackEntry
        coroutineScope.launch {
            editProfileViewModel.onSubmit({
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.edit_profile_success),
                    Toast.LENGTH_SHORT
                ).show()
                if (entry == navController.currentBackStackEntry) navController.navigateUp()
            })
        }
    }

    FormScreen(
        title = stringResource(R.string.edit_profile),
        fieldArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gap_medium), Alignment.Top),
        fieldContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gap_small)),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProfilePicture(
                    selectedImageUri, modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .aspectRatio(1f)
                )
                TextButton(onClick = {
                    chooseImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(text = stringResource(R.string.choose_image))
                }
            }
            UsernameTextBox(
                wrapper = editProfileViewModel.usernameUiState,
                enabled = available,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSubmit()
                },
            )
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = ::onSubmit,
                topButtonText = stringResource(R.string.save),
                onBottomButtonPressed = {
                    navController.navigateUp()
                },
                bottomButtonText = stringResource(R.string.cancel),
                enabled = available
            )
        },
        fieldGap = dimensionResource(R.dimen.gap_medium),
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp,
        enabled = available,
    )
}