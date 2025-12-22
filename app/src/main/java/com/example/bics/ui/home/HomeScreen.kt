package com.example.bics.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.ui.schedule.ShiftBox
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.ProfilePicture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
) {
    val homeViewModel: HomeViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
    val profile by homeViewModel.user.collectAsState()
    val shifts by homeViewModel.shifts.collectAsState()
    val isRefreshing by homeViewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.refresh()
    }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
            .padding(innerPadding)
        ) {
            PullToRefreshBox(
                state = rememberPullToRefreshState(),
                onRefresh = homeViewModel::refresh,
                isRefreshing = isRefreshing
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = profile.username, style = Typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
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
                            .height(400.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Today's Schedule")
                            TextButton(
                                onClick = {
                                    navController.navigate(AppScreen.ScheduleList.name) {
                                        launchSingleTop = true
                                    }
                                }
                            ) {
                                Text(text = "View All", style = Typography.titleMedium)

                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            items(shifts) {
                                val shift by it.collectAsState()
                                ShiftBox(shift) {
                                    navController.navigate("${AppScreen.ShiftDetails.name}/${shift.shiftID}")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                    ) {
                        MenuButton(
                            icon = R.drawable.products2,
                            text = "Products",
                            onClick = { navController.navigate(AppScreen.ProductList.name) }
                        )

                        MenuButton(
                            icon = R.drawable.dispatches2,
                            text = "Dispatches",
                            onClick = { navController.navigate(AppScreen.DispatchList.name) { launchSingleTop = true } }
                        )

                        MenuButton(
                            icon = R.drawable.report2,
                            text = "Generate Report",
                            onClick = { navController.navigate(AppScreen.Report.name) { launchSingleTop = true } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = text,
            fontSize = 18.sp,
            style = Typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        )

    }
}