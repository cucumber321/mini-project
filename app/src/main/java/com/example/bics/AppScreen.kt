package com.example.bics

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bics.ui.theme.BICSTheme
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.data.AppScreen
import com.example.bics.ui.dispatch.screen.AddEditDispatchScreen
import com.example.bics.ui.dispatch.screen.DispatchDetailsScreen
import com.example.bics.ui.dispatch.screen.DispatchListScreen
import com.example.bics.ui.dispatch.screen.FilterDispatchScreen
import com.example.bics.ui.dispatch.screen.SelectDispatchProductsScreen
import com.example.bics.ui.dispatch.viewmodel.AddDispatchViewModel
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import com.example.bics.ui.dispatch.viewmodel.EditDispatchViewModel
import com.example.bics.ui.home.HomeScreen
import com.example.bics.ui.schedule.screen.AddEditShiftScreen
import com.example.bics.ui.schedule.screen.FilterScheduleScreen
import com.example.bics.ui.schedule.screen.ShiftDetailsScreen
import com.example.bics.ui.schedule.screen.ScheduleListScreen
import com.example.bics.ui.schedule.screen.SelectUsersScreen
import com.example.bics.ui.schedule.viewmodel.AddShiftViewModel
import com.example.bics.ui.schedule.viewmodel.EditShiftViewModel
import com.example.bics.ui.schedule.viewmodel.ScheduleViewModelProvider
import com.example.bics.ui.user.screen.ChangeEmailScreen
import com.example.bics.ui.user.screen.ChangePasswordScreen
import com.example.bics.ui.user.screen.ConfirmPasswordScreen
import com.example.bics.ui.user.screen.ContactUsScreen
import com.example.bics.ui.user.screen.EditProfileScreen
import com.example.bics.ui.user.screen.ForgotPasswordScreen
import com.example.bics.ui.user.screen.LoginScreen
import com.example.bics.ui.user.screen.SecuritySettingsScreen
import com.example.bics.ui.user.screen.SignupScreen
import com.example.bics.ui.user.screen.SuccessScreen
import com.example.bics.ui.user.screen.UserMenuScreen
import com.example.bics.ui.user.viewmodel.AuthViewModel
import com.example.bics.ui.user.viewmodel.UserViewModelProvider
import com.example.bics.ui.product.screen.EditProductScreen
import com.example.bics.ui.product.screen.ProductListScreen
import com.example.bics.ui.product.screen.ProductScreen
import com.example.bics.ui.product.screen.ViewProductScreen
import com.example.bics.ui.product.viewmodel.ProductViewModel
import com.example.bics.ui.product.viewmodel.ProductViewModelProvider
import com.example.bics.ui.report.Report

@SuppressLint("LocalContextResourcesRead")
@ExperimentalMaterial3Api
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = UserViewModelProvider.Factory)
    val profile by authViewModel.profile.collectAsState()
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    ErrorCodeLaunchedEffect(error, LocalContext.current)

    BICSTheme {
        if (user != null && profile.uid.isEmpty()) {
            Scaffold { innerPadding -> Surface(Modifier.padding(innerPadding)) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.size(50.dp))
                    Text(stringResource(R.string.loading))
                }
            } }
            return@BICSTheme
        }
        NavHost(
            navController = navController,
            startDestination =
                if (user == null) AppScreen.Login.name
                else AppScreen.Home.name,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutHorizontally { it } },
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = AppScreen.Login.name,
                enterTransition = { scaleIn() },
                popEnterTransition = { EnterTransition.None }
            ) {
                LoginScreen(navController)
            }

            composable(route = AppScreen.Signup.name) {
                SignupScreen(navController)
            }

            composable(route = AppScreen.UserMenu.name,
                enterTransition = { scaleIn() },
                popEnterTransition = { EnterTransition.None},
                exitTransition = { ExitTransition.None},
                popExitTransition = { scaleOut() },
            ) {
                val context = LocalContext.current

                UserMenuScreen(
                    authViewModel = authViewModel,
                    navController = navController
                ) {
                    authViewModel.logout()
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.logout_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            composable(route = AppScreen.EditProfile.name)
            {
                EditProfileScreen(navController)
            }

            composable(route = AppScreen.ConfirmPassword.name) {
                ConfirmPasswordScreen(navController)
            }

            composable(route = AppScreen.SecuritySettings.name)
            {
                val context = LocalContext.current

                SecuritySettingsScreen(
                    navController = navController,
                    onBackButtonPressed = navController::navigateUp,
                    onDeleteUserPressed = {
                        authViewModel.deleteAccount()
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.delete_account_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            composable(route = AppScreen.ChangePassword.name) {
                ChangePasswordScreen(navController)
            }

            composable(route = AppScreen.ForgotPassword.name) {
                ForgotPasswordScreen(navController)
            }

            composable(route = "${AppScreen.Success.name}/{messageID}",
                arguments = listOf(
                    navArgument("messageID") {type = NavType.IntType},
                )
            ) { navBackStackEntry ->
                val message = navBackStackEntry.arguments?.getInt("messageID")

                if (message == null) navController.navigateUp()
                else {
                    SuccessScreen(
                        title = stringResource(R.string.success),
                        message = stringResource(message),
                    ) {
                        navController.navigateUp()
                    }
                }
            }
            composable(route = AppScreen.Contact.name) {
                ContactUsScreen {
                    navController.navigateUp()
                }
            }
            composable(route = AppScreen.Home.name,
                enterTransition = { EnterTransition.None }
            ) {
                HomeScreen(navController)
            }
            composable(route = AppScreen.ChangeEmail.name) {
                ChangeEmailScreen(navController)
            }
            composable(route = AppScreen.ScheduleList.name) { entry ->
                ScheduleListScreen(navController)
            }
            composable(route = AppScreen.FilterSchedule.name) {
                FilterScheduleScreen(navController)
            }
            composable(route = AppScreen.SelectUsers.name) {
                SelectUsersScreen(navController)
            }
            composable(route = AppScreen.AddShift.name) {
                val viewModel: AddShiftViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
                AddEditShiftScreen(navController, viewModel, "Add")
            }
            composable(route = AppScreen.EditShift.name) {
                val viewModel: EditShiftViewModel = viewModel(factory = ScheduleViewModelProvider.Factory)
                AddEditShiftScreen(navController, viewModel, "Edit")
            }
            composable(route = "${AppScreen.ShiftDetails.name}/{shiftID}",
                arguments = listOf(
                    navArgument("shiftID") {type = NavType.StringType},
                )
            ) { backStackEntry ->

                val shiftID = backStackEntry.arguments?.getString("shiftID")
                if (shiftID == null) navController.navigateUp()
                else {
                    ShiftDetailsScreen(navController, shiftID)
                }

            }
            composable(AppScreen.ProductList.name) {
                ProductListScreen(
                    onAddClick = { navController.navigate(AppScreen.AddProduct.name) { launchSingleTop = true } },
                    onItemClick = { id -> navController.navigate("${AppScreen.ViewProduct.name}/$id") { launchSingleTop = true } },
                    onBackButtonClick = navController::navigateUp
                )
            }

            composable(AppScreen.AddProduct.name) { backStack ->
                val viewModel: ProductViewModel = viewModel(factory = ProductViewModelProvider.Factory)

                ProductScreen(
                    onCancel = navController::navigateUp,
                    viewModel = viewModel,
                    title = "Add Product"
                )
            }

            composable("${AppScreen.ViewProduct.name}/{id}") { backStack ->
                val id = backStack.arguments?.getString("id") ?: ""
                val back: () -> Unit = {
                    navController.currentBackStackEntry?.let{
                        if (it == backStack) navController.navigateUp()
                    }
                }
                ViewProductScreen(
                    productId = id,
                    onEdit = { navController.navigate("${AppScreen.EditProduct.name}/$id") { launchSingleTop = true } },
                    onDelete = back,
                    onCancel = back,
                    onBack = back
                )
            }

            composable("${AppScreen.EditProduct.name}/{id}") { backStack ->
                val id = backStack.arguments?.getString("id") ?: ""
                val back: () -> Unit = {
                    navController.currentBackStackEntry?.let{
                        if (it == backStack) navController.navigateUp()
                    }
                }
                EditProductScreen(
                    productId = id,
                    onCancel = back
                )
            }
            composable(route = AppScreen.Report.name) {
                Report(navController::navigateUp)
            }
            composable(route = AppScreen.FilterDispatch.name) {
                FilterDispatchScreen(navController)
            }
            composable(route = AppScreen.DispatchList.name) {
                DispatchListScreen(navController)
            }
            composable(route = AppScreen.AddDispatch.name) {
                val viewModel: AddDispatchViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
                AddEditDispatchScreen(navController, "Add", viewModel)
            }
            composable(route = AppScreen.EditDispatch.name) {
                val viewModel: EditDispatchViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
                AddEditDispatchScreen(navController, "Edit", viewModel)
            }
            composable(route = AppScreen.SelectDispatchProducts.name) {
                SelectDispatchProductsScreen(navController)
            }
            composable(route = "${AppScreen.DispatchDetails.name}/{dispatchId}",
                arguments = listOf(
                    navArgument("dispatchId") {type = NavType.StringType},
                )
            ) { entry ->
                val dispatchId = entry.arguments?.getString("dispatchId") ?: ""
                if (dispatchId.isBlank()) navController.navigateUp()

                DispatchDetailsScreen(navController, dispatchId)
            }
        }
    }
}
