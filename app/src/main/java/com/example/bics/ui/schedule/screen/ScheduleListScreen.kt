package com.example.bics.ui.schedule.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.ui.schedule.CustomDatePicker
import com.example.bics.ui.schedule.CustomFloatingButton
import com.example.bics.ui.schedule.ShiftBox
import com.example.bics.ui.schedule.viewmodel.ScheduleListViewModel
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.user.UserTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    navController: NavController
) {
    val scheduleListViewModel: ScheduleListViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
    val shifts by scheduleListViewModel.shifts.collectAsState()
    val isRefreshing by scheduleListViewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        scheduleListViewModel.refresh()
    }

    Scaffold(
        topBar = {
            UserTopBar(
                title = "Schedule",
                showBackButton = true,
                onBackButtonPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            PullToRefreshBox(
                state = rememberPullToRefreshState(),
                isRefreshing = isRefreshing,
                onRefresh = scheduleListViewModel::refresh
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CustomDatePicker {
                            instant -> instant?.let { scheduleListViewModel.onDateSelected(it) }
                        }
                        IconButton(
                            onClick = {
                                navController.navigate(AppScreen.FilterSchedule.name) {launchSingleTop = true}
                            }
                        ) {
                            Icon(painter = painterResource(R.drawable.filter), contentDescription = "Filter", modifier = Modifier.size(24.dp))
                        }
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(shifts) {
                            val shift by it.collectAsState()
                            ShiftBox(shift) {
                                navController.navigate("${AppScreen.ShiftDetails.name}/${shift.shiftID}") { launchSingleTop = true }
                            }
                        }
                    }
                }

                CustomFloatingButton(R.drawable.add) {
                    navController.navigate(AppScreen.AddShift.name) { launchSingleTop = true }
                }
            }
        }
    }
}