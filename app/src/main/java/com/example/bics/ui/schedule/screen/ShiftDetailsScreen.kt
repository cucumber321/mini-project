package com.example.bics.ui.schedule.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.ui.schedule.CustomFloatingButton
import com.example.bics.ui.schedule.SelectUsersList
import com.example.bics.ui.schedule.formatDate
import com.example.bics.ui.schedule.formatTime
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.schedule.viewmodel.ShiftDetailsViewModel
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.BackButton
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftDetailsScreen(navController: NavController, shiftID: String) {
    val shiftDetailsViewModel: ShiftDetailsViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
    val loading by shiftDetailsViewModel.loading.collectAsState()
    val shift by shiftDetailsViewModel.shift.collectAsState()
    val profiles by shiftDetailsViewModel.profiles.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val date = shift
        .startDate
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toEpochSecond()

    LaunchedEffect(Unit) {
        shiftDetailsViewModel.loadDetails(shiftID)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Shift Details") },
                navigationIcon = { BackButton(onClick = navController::navigateUp, enabled = true, icon = R.drawable.back2) },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete",
                            modifier = Modifier.fillMaxSize(0.8f)
                        )
                    }
                          },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (loading) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.size(50.dp))
                    Text(stringResource(R.string.loading))
                }
                return@Surface
            }

            if (showDialog) {
                BasicAlertDialog(
                    onDismissRequest = {showDialog = false},
                ) {
                    Card(Modifier.aspectRatio(2f).padding(dimensionResource(R.dimen.padding_small))) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f).padding(dimensionResource(R.dimen.padding_medium))
                        ) {
                            Text(text = "Are you sure you want to delete this shift?", textAlign = TextAlign.Justify)
                        }
                        Row(
                            horizontalArrangement = Arrangement
                                .spacedBy(
                                    dimensionResource(R.dimen.gap_medium),
                                    Alignment.CenterHorizontally
                                ),
                            modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.padding_small))
                        ) {
                            TextButton(onClick = {showDialog = false}) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            TextButton(onClick = {
                                shiftDetailsViewModel.onDelete(shiftID)
                                Toast.makeText(
                                    context,
                                    "Shift Successfully Deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigateUp()
                            }) {
                                Text(text = stringResource(R.string.confirm), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(shift.shiftID)
                Text(shift.title, style = Typography.titleLarge, modifier = Modifier.padding(PaddingValues(bottom = 10.dp)))
                DetailsRow(
                    "Shift Date",
                    Instant
                        .ofEpochSecond(date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .formatDate()
                )
                DetailsRow(
                    "Start Time",
                    shift
                        .startDate
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime()
                        .formatTime()
                )
                DetailsRow(
                    "End Time",
                    shift
                        .endDate
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime()
                        .formatTime()
                )
                SelectUsersList(
                    profiles,
                    viewOnly = true,
                    title = "Users Assigned"
                )
                Text("Description", style = Typography.titleMedium)
                Text(shift.description.ifEmpty { "No Description" })
            }
            CustomFloatingButton(
                icon = R.drawable.edit
            ) {
                shiftDetailsViewModel.setChosenShiftState(shiftID)
                navController.navigate(AppScreen.EditShift.name) { launchSingleTop = true }
            }
        }

    }
}

@Composable
private fun DetailsRow(title: String, content: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = Typography.titleMedium)
        Text(content)
    }
}