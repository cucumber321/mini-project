package com.example.bics.ui.dispatch.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.bics.data.AppScreen
import com.example.bics.data.user.ErrorCode
import com.example.bics.ui.dispatch.SelectedProductsList
import com.example.bics.ui.dispatch.viewmodel.DispatchFormViewModel
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.ErrorCodeLaunchedEffect
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickOptionButtons
import com.example.bics.ui.user.UserTextBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDispatchScreen(navController: NavController, keyword: String, viewModel: DispatchFormViewModel) {
    val orderedBy by viewModel.orderedBy.uiState.collectAsState()
    val productList by viewModel.productList.uiState.collectAsState()
    val show by viewModel.showConfirm.collectAsState()
    val enabled by viewModel.available.collectAsState()
    val error by viewModel.error.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ErrorCodeLaunchedEffect(error, context)

    FormScreen(
        title = "$keyword Dispatch",
        fieldContent = {
            UserTextBox(
                upperLabel = "Ordered By",
                value = orderedBy.fieldInput,
                onValueChange = viewModel.orderedBy::onValueChanged,
                isError = orderedBy.errorCode != ErrorCode.None,
                errorMessage = stringResource(orderedBy.errorCode.errorMessage),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            )
            SelectedProductsList(
                productList.fieldInput.sortedBy { it.product.name },
                viewOnly = false,
                onAddButtonClick = {
                    viewModel.updateSharedList()
                    navController.navigate(AppScreen.SelectDispatchProducts.name) {
                        launchSingleTop = true
                    }
                },
                onQuantityIncrease = viewModel::onQuantityIncrease,
                onQuantityDecrease = viewModel::onQuantityDecrease,
                onQuantityChange = viewModel::onQuantityChange,
                onRemove = viewModel::onRemove,
                onDismiss = viewModel::onDismiss,
                show = show,
                enabled = enabled
            )
            Text(text = stringResource(productList.errorCode.errorMessage), style = Typography.bodySmall, color = MaterialTheme.colorScheme.error)
        },
        buttons = {
            ThickOptionButtons(
                onTopButtonPressed = {
                    coroutineScope.launch {
                        val entry = navController.currentBackStackEntry
                        viewModel.onSubmit(onSuccess =  {
                            Toast.makeText(
                                context,
                                "Dispatch Successfully ${keyword}ed",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (navController.currentBackStackEntry == entry) navController.navigateUp()
                        })
                    }
                },
                topButtonText = keyword,
                onBottomButtonPressed = navController::navigateUp,
                bottomButtonText = "Cancel",
                enabled = enabled
            )
        },
        enabled = enabled,
        showBackButton = true,
        onBackButtonPressed = navController::navigateUp
    )
}