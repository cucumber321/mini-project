package com.example.bics.ui.schedule.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.ui.schedule.CustomFloatingButton
import com.example.bics.ui.schedule.SearchBar
import com.example.bics.ui.schedule.SelectUserIcon
import com.example.bics.ui.schedule.SelectUserRow
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.schedule.viewmodel.SelectUsersViewModel
import com.example.bics.ui.user.UserTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectUsersScreen(navController: NavController) {
    val selectUsersViewModel: SelectUsersViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
    val userList by selectUsersViewModel.userList.collectAsState()
    val chosen by selectUsersViewModel.chosen.collectAsState()
    val search by selectUsersViewModel.search.collectAsState()

    Scaffold(
        topBar = {
            UserTopBar(
                title = "Select Users",
                showBackButton = true,
                onBackButtonPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                SearchBar(
                    search,
                    selectUsersViewModel::onSearch,
                    placeholder = "Search by user ID or name...",
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                HorizontalDivider()
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    items(chosen) {
                        SelectUserIcon(it, modifier = Modifier.size(64.dp)) {
                            selectUsersViewModel.onRemove(it)
                        }
                    }
                }
                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(userList) {
                        if (it.uid.contains(search, true) || it.username.contains(search, true)) {
                            SelectUserRow(it, chosen.contains(it)) {
                                selectUsersViewModel.onSelect(it)
                            }
                        }
                    }
                }
            }

            CustomFloatingButton(R.drawable.check2) {
                selectUsersViewModel.onConfirm()
                navController.navigateUp()
            }
        }
    }
}