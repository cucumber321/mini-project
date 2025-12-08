package com.example.bics.ui.schedule.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.data.AppScreen
import com.example.bics.ui.schedule.CustomRadioButton
import com.example.bics.ui.schedule.SelectUsersList
import com.example.bics.ui.schedule.viewmodel.FilterScheduleViewModel
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScheduleScreen(navController: NavController) {
    val filterScheduleViewModel: FilterScheduleViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
    val users by filterScheduleViewModel.users.collectAsState()
    val includeAll by filterScheduleViewModel.includeAll.collectAsState()

    FormScreen(
        title = "Filter Schedule",
        fieldContent = {
            SelectUsersList(
                users,
                "Staff Assigned",
                onRemoveUserClick = { filterScheduleViewModel.onRemoveUser(it) },
                onAddUserClick = {
                    navController.navigate(AppScreen.SelectUsers.name) {launchSingleTop = true}
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                CustomRadioButton(includeAll, "Include All") {
                    filterScheduleViewModel.onIncludeAllSelected(true)
                }
                CustomRadioButton(!includeAll, "Include Any") {
                    filterScheduleViewModel.onIncludeAllSelected(false)
                }
            }
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = {
                    filterScheduleViewModel.onApply()
                    navController.navigateUp()
                },
                topButtonText = "Apply",
                onBottomButtonPressed = navController::navigateUp,
                bottomButtonText = "Cancel",
                enabled = true
            )
        },
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp,
        fieldArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    )
}