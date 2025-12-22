package com.example.bics.ui.user

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bics.R
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.PasswordType
import com.example.bics.data.user.UserMenuOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    showBackButton: Boolean = false,
    showActions: Boolean = false,
    onBackButtonPressed: () -> Unit = {},
    onActionsPressed: () -> Unit = {},
    @DrawableRes backButtonIcon: Int = R.drawable.arrow_left,
    @DrawableRes actionsIcon: Int = R.drawable.delete,
    enabled: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (showBackButton) {
                BackButton(
                    onClick = onBackButtonPressed,
                    enabled = enabled,
                    icon = backButtonIcon
                )
            }
        },
        actions = {
            if (showActions) {
                BackButton(
                    onClick = onActionsPressed,
                    enabled = enabled,
                    icon = actionsIcon,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit, enabled: Boolean, @DrawableRes icon: Int, tint: Color = LocalContentColor.current) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(R.string.back_button),
            modifier = modifier.fillMaxSize(0.8f),
            tint = tint
        )
    }
}

@Composable
fun UserTextBox(
    modifier: Modifier = Modifier,
    upperLabel: String = "",
    placeholder: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true,
    trailingIcon: @Composable () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text(text = upperLabel)},
        placeholder = { Text(text = placeholder, color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified) },
        singleLine = maxLines == 1,
        isError = isError,
        supportingText = {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        },
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner)),
        keyboardActions = keyboardActions,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        maxLines = maxLines,
        modifier = modifier
    )
}

@Composable
fun ThickButton(
    modifier: Modifier = Modifier,
    text: String = "",
    enabled: Boolean = true,
    outline: Boolean = false,
    onClick: () -> Unit = {},
) {
    if (outline) {
        OutlinedButton (
            onClick = onClick,
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner)),
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.thick_button_height))
        ) {
            Text(text = text)
        }
    } else {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner)),
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.thick_button_height))
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun UsernameTextBox(
    wrapper: FieldUiStateWrapper<String>,
    enabled: Boolean,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val uiState by wrapper.uiState.collectAsState()

    UserTextBox(
        modifier = Modifier.fillMaxWidth(),
        upperLabel = "${stringResource(R.string.username)}: ",
        value = uiState.fieldInput,
        onValueChange = wrapper::onValueChanged,
        isError = uiState.errorCode != ErrorCode.None,
        errorMessage = stringResource((uiState.errorCode).errorMessage),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = keyboardActions,
        enabled = enabled,
    )
}

@Composable
fun EmailTextBox(
    wrapper: FieldUiStateWrapper<String>,
    enabled: Boolean,
    @StringRes title: Int = R.string.email,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val uiState by wrapper.uiState.collectAsState()

    UserTextBox(
        modifier = Modifier.fillMaxWidth(),
        upperLabel = stringResource(title),
        placeholder = stringResource(R.string.email_placeholder),
        value = uiState.fieldInput,
        onValueChange = wrapper::onValueChanged,
        isError = uiState.errorCode in listOf(
            ErrorCode.EmptyEmail,
            ErrorCode.InvalidEmailFormat,
            ErrorCode.EmailInUse,
            ErrorCode.InvalidCredentials,
            ErrorCode.SameEmail
        ),
        errorMessage = stringResource(
            (
                    if (uiState.errorCode in listOf(
                            ErrorCode.EmptyEmail,
                            ErrorCode.InvalidEmailFormat,
                            ErrorCode.EmailInUse,
                            ErrorCode.SameEmail
                    )
                        )
                        uiState.errorCode else ErrorCode.None
                    ).errorMessage
        ),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = KeyboardType.Email),
        keyboardActions = keyboardActions,
        enabled = enabled
    )
}

@Composable
fun PasswordTextBox(
    wrapper: FieldUiStateWrapper<String>,
    enabled: Boolean,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onForgotPasswordButtonPressed: () -> Unit = {},
    includeForgotPassword: Boolean = false,
    passwordType: PasswordType = PasswordType.Password,
) {
    val uiState by wrapper.uiState.collectAsState()

    var showPassword by remember { mutableStateOf(false) }

    Column {
        UserTextBox(
            modifier = Modifier.fillMaxWidth(),
            upperLabel = stringResource(passwordType.upperLabel),
            value = uiState.fieldInput,
            onValueChange = wrapper::onValueChanged,
            isError = passwordType.isError(uiState.errorCode),
            errorMessage = stringResource(passwordType.errorMessage(uiState.errorCode).errorMessage),
            keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = KeyboardType.Password),
            keyboardActions = keyboardActions,
            enabled = enabled,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                ShowHideButton(showPassword, enabled) {showPassword = !showPassword}
            }
        )
        if (includeForgotPassword) {
            TextButton(onClick = onForgotPasswordButtonPressed, enabled = enabled) {
                Text(text = stringResource(R.string.forgot_password) + "?", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ShowHideButton(showPassword: Boolean, enabled: Boolean, onButtonPressed: () -> Unit) {
    IconButton(onClick = onButtonPressed, enabled = enabled) {
        Icon(
            painter = painterResource(if (showPassword) R.drawable.show_password else R.drawable.hide_password),
            contentDescription = stringResource(if (showPassword) R.string.show_password else R.string.hide_password),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun CenteredOptionButtons(
    onTopButtonPressed: () -> Unit = {},
    topButtonText: String = "",
    onBottomButtonPressed: () -> Unit = {},
    bottomButtonText: String = "",
    enabled: Boolean = true,
) {
    Button(onClick = onTopButtonPressed, enabled = enabled) {
        Text(text = topButtonText)
    }
    TextButton(onClick = onBottomButtonPressed, enabled = enabled) {
        Text(text = bottomButtonText)
    }

}

@Composable
fun ThickOptionButtons(
    onTopButtonPressed: () -> Unit,
    topButtonText: String,
    onBottomButtonPressed: () -> Unit,
    bottomButtonText: String,
    enabled: Boolean,
) {
    ThickButton(text = topButtonText, onClick = onTopButtonPressed, enabled = enabled)
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.gap_small)))
    ThickButton(text = bottomButtonText, onClick = onBottomButtonPressed, enabled = enabled, outline = true)
}

@Composable
fun ProfilePicture(fullImageUrl: Uri?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(fullImageUrl)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.profile_picture),
        placeholder = painterResource(R.drawable.profile_picture_default),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(CircleShape)
    )
}


@Composable
fun MenuOption(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.arrow_right,
    color: Color = Color.Unspecified,
    onClick: () -> Unit,
) {
    TextButton(
        shape = RectangleShape,
        onClick = onClick,
        modifier = modifier.height(dimensionResource(R.dimen.menu_option_height)),
    ) {
        Text(text = stringResource(text), color = color, modifier = Modifier.weight(1f))
        Icon(painter = painterResource(icon), tint = if (color == Color.Unspecified) LocalContentColor.current else color, contentDescription = stringResource(R.string.continue_text), modifier = Modifier.fillMaxHeight(0.3f))
    }
}

@Composable
fun MenuOptionList(options: List<UserMenuOption>, navController: NavController, modifier: Modifier = Modifier) {
    for (option in options) {
        MenuOption(
            modifier = modifier,
            text = option.text
        ) {
            navController.navigate(option.navigateTo.name) {launchSingleTop = true}
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline))
    }
}

@Composable
fun ErrorCodeLaunchedEffect(errorCode: ErrorCode, context: Context) {
    if (errorCode == ErrorCode.None) return
    LaunchedEffect(errorCode) {
        Toast.makeText(
            context,
            errorCode.errorMessage,
            Toast.LENGTH_LONG
        ).show()
    }
}