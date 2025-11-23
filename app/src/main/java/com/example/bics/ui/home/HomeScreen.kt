package com.example.bics.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bics.data.AppScreen
import com.example.bics.ui.user.ProfilePicture
import com.example.bics.ui.user.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val profile by authViewModel.profile.collectAsState()

    Scaffold { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                ) {
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
                                navController.navigate(AppScreen.UserMenu.name) {launchSingleTop = true}
                            }
                    )

                }
            }
        }
    }
}