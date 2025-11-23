package com.example.bics.ui.user.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.example.bics.R
import com.example.bics.data.user.UserDataSource
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.UserTopBar

@ExperimentalMaterial3Api
@Composable
fun ContactUsScreen(onBackButtonPressed: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            UserTopBar(
                title = stringResource(R.string.contact_us),
                showBackButton = true,
                onBackButtonPressed = onBackButtonPressed
            )
        }
    ) {innerPadding ->

        Surface(Modifier.padding(innerPadding)) {
            Column {
                Option(
                    R.string.phone_number,
                    UserDataSource.CONTACT_PHONE_NUMBER,
                    R.drawable.dial,
                    onClick = {dialNumber(context)},
                    onHold = {copyText(context, UserDataSource.CONTACT_PHONE_NUMBER, context.resources.getString(R.string.phone_number))}
                )
                Option(
                    R.string.contact_email,
                    UserDataSource.CONTACT_EMAIL,
                    R.drawable.email,
                    onClick = {composeEmail(context)},
                    onHold = {copyText(context, UserDataSource.CONTACT_EMAIL, context.resources.getString(R.string.contact_email))}
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Option(
    @StringRes title: Int,
    content: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    onHold: () -> Unit
) {
    Box (
        Modifier
        .fillMaxWidth()
        .height(dimensionResource(R.dimen.menu_option_height))
        .combinedClickable(onClick = onClick, onLongClick = onHold)
    ){
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(title),
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = content,
                    style = Typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxHeight(0.4f)
            )
        }
    }
}

fun copyText(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)

    clipboard.setPrimaryClip(clip)
}

private fun dialNumber(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:${UserDataSource.CONTACT_PHONE_NUMBER}".toUri()
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun composeEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(UserDataSource.CONTACT_EMAIL))
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.contact_email)))
    }
}