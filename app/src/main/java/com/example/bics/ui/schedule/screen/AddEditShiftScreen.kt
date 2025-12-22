package com.example.bics.ui.schedule.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.data.user.ErrorCode
import com.example.bics.ui.schedule.CustomDatePicker
import com.example.bics.ui.schedule.CustomTimePicker
import com.example.bics.ui.schedule.SelectUsersList
import com.example.bics.ui.schedule.viewmodel.ScheduleFormViewModel
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.UserTextBox
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditShiftScreen(navController: NavController, viewModel: ScheduleFormViewModel, keyword: String) {
    val enabled by viewModel.available.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val title by viewModel.titleUiState.uiState.collectAsState()
    val description by viewModel.descriptionUiState.uiState.collectAsState()
    val dateUiState by viewModel.dateUiState.uiState.collectAsState()
    val users by viewModel.usersUiState.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ErrorCodeLaunchedEffect(error, context)

    if (loading) {
        Scaffold { innerPadding -> Surface(Modifier.padding(innerPadding)) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.size(50.dp))
                Text(stringResource(R.string.loading))
            }
        } }
        return
    }

    FormScreen(
        title = "$keyword Shift",
        fieldContent = {
            UserTextBox(
                upperLabel = "Title:",
                value = title.fieldInput,
                onValueChange = viewModel.titleUiState::onValueChanged,
                isError = title.errorCode == ErrorCode.EmptyTitle,
                errorMessage = stringResource(title.errorCode.errorMessage),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Date:")
                Column(horizontalAlignment = Alignment.End) {
                    CustomDatePicker(
                        onDateSelected =  viewModel.dateUiState::onValueChanged,
                        isError = dateUiState.errorCode != ErrorCode.None,
                        enabled = enabled,
                        initialDateMilli = dateUiState
                            .fieldInput
                            ?.atZone(ZoneId.systemDefault())
                            ?.toLocalDate()
                            ?.atStartOfDay(ZoneOffset.UTC)
                            ?.toInstant()
                            ?.toEpochMilli()
                    )
                    Text(
                        text = stringResource(dateUiState.errorCode.errorMessage),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Start Time:")
                CustomTimePicker(
                    wrapper = viewModel.startTimeUiState,
                    error = { ErrorCode.None },
                    enabled = enabled
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "End Time:")
                CustomTimePicker(
                    wrapper = viewModel.endTimeUiState,
                    error = { if (it == ErrorCode.InvalidTimeRange) it else ErrorCode.None },
                    enabled = enabled
                )
            }
            SelectUsersList(
                usersUiState = users.fieldInput,
                errorCode = users.errorCode,
                title = "Users Assigned",
                onAddUserClick = {navController.navigate(AppScreen.SelectUsers.name) {launchSingleTop = true} },
                onRemoveUserClick = viewModel::onRemoveUser,
                enabled = enabled
                )
            UserTextBox(
                upperLabel = "Description:",
                value = description.fieldInput,
                onValueChange = viewModel.descriptionUiState::onValueChanged,
                enabled = enabled,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth()
            )
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = {
                    coroutineScope.launch {
                        val stack = navController.currentBackStackEntry
                        viewModel.onSubmit(
                            onSuccess = {
                            Toast.makeText(
                                context,
                                "Shift Successfully ${keyword}ed",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (navController.currentBackStackEntry == stack) navController.navigateUp()
                        }, onFailure = {
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_LONG
                            ).show()
                        })
                    }
                },
                topButtonText = keyword,
                onBottomButtonPressed = navController::navigateUp,
                bottomButtonText = "Cancel",
                enabled = enabled
            )
        },
        enabled = enabled,
        fieldWeight = 10f,
        showBackButton = true,
        fieldArrangement = Arrangement.Top,
        onBackButtonPressed = navController::navigateUp,
    )
}