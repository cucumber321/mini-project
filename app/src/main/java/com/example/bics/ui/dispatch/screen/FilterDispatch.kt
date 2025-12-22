package com.example.bics.ui.dispatch.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.data.user.ErrorCode
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import com.example.bics.ui.dispatch.viewmodel.FilterDispatchViewModel
import com.example.bics.ui.schedule.CustomDatePicker
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDispatchScreen(
    navController: NavController
) {
    val filterDispatchViewModel: FilterDispatchViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
    val startDate by filterDispatchViewModel.startDate.uiState.collectAsState()
    val endDate by filterDispatchViewModel.endDate.uiState.collectAsState()
    val startDateState = rememberDatePickerState(
        initialSelectedDateMillis = startDate.fieldInput?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                ?.toLocalDate()
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        }
    )
    val endDateState = rememberDatePickerState(
        initialSelectedDateMillis = endDate.fieldInput?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                ?.toLocalDate()
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        }
    )

    FormScreen(
        title = "Filter Dispatch",
        fieldContent = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Date From:")
                Column(horizontalAlignment = Alignment.End) {
                    CustomDatePicker(
                        onDateSelected = {
                            filterDispatchViewModel.startDate.onValueChanged(it?.epochSecond)
                                         },
                        isError = startDate.errorCode != ErrorCode.None,
                        datePickerState = startDateState
                    )
                    Text(
                        text = "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Date Until:")
                Column(horizontalAlignment = Alignment.End) {
                    CustomDatePicker(
                        onDateSelected =  {
                            filterDispatchViewModel.endDate.onValueChanged(it?.epochSecond)
                        },
                        isError = endDate.errorCode != ErrorCode.None,
                        datePickerState = endDateState
                    )
                    Text(
                        text = stringResource(endDate.errorCode.errorMessage),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = {
                    filterDispatchViewModel.onSubmit {
                        navController.navigateUp()
                    }
                },
                topButtonText = "Apply",
                onBottomButtonPressed = {
                    filterDispatchViewModel.reset()
                    startDateState.selectedDateMillis = null
                    endDateState.selectedDateMillis = null
                },
                bottomButtonText = "Reset",
                enabled = true
            )
        },
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp
    )
}