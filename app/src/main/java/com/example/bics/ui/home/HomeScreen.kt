package com.example.bics.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bics.data.AppScreen
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.ProfilePicture
import com.example.bics.ui.user.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val profile by authViewModel.profile.collectAsState()
//    val homeViewModel: HomeViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)

    Scaffold { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = profile.username, style = Typography.titleLarge)
                    ProfilePicture(
                        fullImageUrl = profile.profilePictureUri,
                        modifier = Modifier
                            .size(61.dp)
                            .border(
                                3.dp,
                                MaterialTheme.colorScheme.inverseOnSurface,
                                CircleShape
                            )
                            .clickable {
                                navController.navigate(AppScreen.UserMenu.name) {
                                    launchSingleTop = true
                                }
                            }
                    )

                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Today's Schedule")
                        TextButton(
                            onClick = {
                                navController.navigate(AppScreen.ScheduleList.name) {launchSingleTop = true}
                            }
                        ) {
                            Text(text = "View All", style = Typography.titleMedium)

                        }
                    }

                }
            }
        }
    }
}