package com.example.bics.ui.user.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.user.UserDataSource
import com.example.bics.ui.user.MenuOption
import com.example.bics.ui.user.MenuOptionList
import com.example.bics.ui.user.UserTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    navController: NavController,
    onBackButtonPressed: () -> Unit,
    onDeleteUserPressed: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UserTopBar(
                title = stringResource(R.string.security_settings),
                showBackButton = true,
                onBackButtonPressed = onBackButtonPressed,
            )
        }
    ) {innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (showDialog) {
                BasicAlertDialog(
                    onDismissRequest = {showDialog = false},
                ) {
                    Card(Modifier.aspectRatio(2f).padding(dimensionResource(R.dimen.padding_small))) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f).padding(dimensionResource(R.dimen.padding_medium))
                        ) {
                            Text(text = stringResource(R.string.delete_account_confirmation), textAlign = TextAlign.Justify)
                        }
                        Row(
                            horizontalArrangement = Arrangement
                                .spacedBy(
                                    dimensionResource(R.dimen.gap_medium),
                                    Alignment.CenterHorizontally
                                ),
                            modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.padding_small))
                        ) {
                            Button(onClick = {showDialog = false}) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            Button(onClick = onDeleteUserPressed) {
                                Text(text = stringResource(R.string.confirm))
                            }
                        }
                    }
                }
            }

            Column {
                MenuOptionList(UserDataSource.securitySettingsOptions, navController)

                MenuOption(
                    text = R.string.delete_account,
                    onClick = { showDialog = true },
                    icon = R.drawable.delete,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}