package com.example.bics.ui.user.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.user.UserDataSource
import com.example.bics.ui.user.MenuOption
import com.example.bics.ui.user.MenuOptionList
import com.example.bics.ui.user.ProfilePicture
import com.example.bics.ui.user.UserTopBar
import com.example.bics.ui.user.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onLogoutButtonPressed: () -> Unit,
) {
    val user = authViewModel.user.collectAsState()
    val profile = authViewModel.profile.collectAsState()
    val localConfiguration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UserTopBar(
                title = stringResource(R.string.menu),
                showBackButton = true,
                onBackButtonPressed = { navController.navigateUp() },
                backButtonIcon = R.drawable.close
            )
        },
    ) {innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        authViewModel.refreshUser()
                        delay(100)
                        isRefreshing = false
                    }
                },
            ) {

                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .height(
                    (if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) localConfiguration.screenHeightDp.dp else localConfiguration.screenWidthDp.dp) - innerPadding.calculateTopPadding()
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gap_medium)),
                        modifier = Modifier
                            .height(150.dp)
                            .padding(horizontal = dimensionResource(R.dimen.padding_medium))


                    ) {
                        ProfilePicture(
                            fullImageUrl = profile.value.profilePictureUri,
                            modifier = Modifier.fillMaxHeight(0.5f).aspectRatio(1f)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = profile.value.username, overflow = TextOverflow.Ellipsis, maxLines = 2)
                            Text(text = user.value?.email ?: "")
                        }
                    }

                    MenuOptionList(UserDataSource.userMenuOptions, navController)

                    MenuOption(
                        text = R.string.logout_text,
                        onClick = onLogoutButtonPressed,
                        icon = R.drawable.logout,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

    }
}
