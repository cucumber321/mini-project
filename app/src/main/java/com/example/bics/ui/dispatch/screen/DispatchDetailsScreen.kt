package com.example.bics.ui.dispatch.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.data.AppScreen
import com.example.bics.ui.dispatch.SelectedProductsList
import com.example.bics.ui.dispatch.viewmodel.DispatchDetailsViewModel
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.ThickButton
import com.example.bics.ui.user.UserTopBar
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchDetailsScreen(navController: NavController, dispatchId: String) {
    val dispatchDetailsViewModel: DispatchDetailsViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
    val dispatch by dispatchDetailsViewModel.dispatch.collectAsState()
    val isLoading by dispatchDetailsViewModel.isLoading.collectAsState()
    val enabled by dispatchDetailsViewModel.enabled.collectAsState()
    val createdBy by dispatchDetailsViewModel.createdBy.collectAsState()
    val lastModifiedBy by dispatchDetailsViewModel.lastModifiedBy.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        dispatchDetailsViewModel.loadDetails(dispatchId)
    }

    Scaffold(
        topBar = {
            UserTopBar(
                title = "Dispatch Details",
                showBackButton = true,
                onBackButtonPressed = navController::navigateUp,
                showActions = true,
                onActionsPressed = { showDialog = true },
                enabled = enabled
            )
        }
    ) { innerPadding ->
        Surface(Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        CircularProgressIndicator()
                        Text("Loading...")
                    }
                }
                return@Surface
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },

                    title = { Text("Confirm Delete") },

                    text = { Text("Are you sure you want to delete this dispatch?") },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                val entry = navController.currentBackStackEntry
                                dispatchDetailsViewModel.deleteDispatch {
                                    Toast.makeText(
                                        context,
                                        "Dispatch Successfully Deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (entry == navController.currentBackStackEntry) navController.navigateUp()
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailsRow("Dispatch ID", dispatch.id)
                DetailsRow("Ordered By", dispatch.orderedBy)
                DetailsRow("Created By", createdBy, dispatch.dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().formatDateTime())
                DetailsRow("Last Modified By", lastModifiedBy, (dispatch.lastModifiedDate?: dispatch.dateCreated).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().formatDateTime())
                SelectedProductsList(dispatch.items, viewOnly = true, onAddButtonClick = {
                    navController.navigate(AppScreen.SelectDispatchProducts.name)
                })
                Spacer(Modifier
                    .weight(1f)
                    .padding(5.dp)
                )
                ThickButton(
                    text = "Edit",
                    enabled = enabled
                ) {
                    dispatchDetailsViewModel.setChosenDispatchId()
                    navController.navigate(AppScreen.EditDispatch.name) { launchSingleTop = true }
                }
            }
        }
    }
}

@Composable
private fun DetailsRow(
    title: String,
    content: String,
    subContent: String = ""
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = Typography.titleMedium)
        Column(horizontalAlignment = Alignment.End) {
            Text(content)
            if (subContent.isNotEmpty()) Text(subContent, style = Typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun LocalDateTime.formatDateTime() = this.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))