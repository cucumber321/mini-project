package com.example.bics.ui.user

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.example.bics.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    title: String,
    fieldContent: @Composable ColumnScope.() -> Unit,
    buttons: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fieldGap: Dp = dimensionResource(R.dimen.gap_small),
    fieldWeight: Float = 1f,
    showBackButton: Boolean = false,
    onBackButtonPressed: () -> Unit = {},
    buttonArrangement: Arrangement.Vertical = Arrangement.Bottom,
    fieldArrangement: Arrangement.Vertical = Arrangement.spacedBy(fieldGap, Alignment.CenterVertically),
) {
    val configuration = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    val scrollBehavior = null
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            UserTopBar(
                scrollBehavior = scrollBehavior,
                title = title,
                showBackButton = showBackButton,
                onBackButtonPressed = onBackButtonPressed,
                enabled = enabled
            )
        },
        modifier = Modifier
//            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .pointerInput(Unit) {
            detectTapGestures {
                focusManager.clearFocus()
            }
        }
    ) {innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = dimensionResource(R.dimen.form_horizontal_padding),
                            vertical = dimensionResource(R.dimen.form_vertical_padding)
                        ),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = fieldArrangement,
                        content = fieldContent,
                        modifier = Modifier.weight(fieldWeight).verticalScroll(rememberScrollState())
                    )

                    Column(
                        verticalArrangement = buttonArrangement,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = buttons,
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gap_small)),
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(R.dimen.form_horizontal_padding),
                            horizontal = dimensionResource(R.dimen.form_vertical_padding)
                        )
                        .fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(fieldGap),
                        content = fieldContent,
                        modifier = Modifier.weight(2f)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        content = buttons,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    )
                }
            }
        }
    }
}
