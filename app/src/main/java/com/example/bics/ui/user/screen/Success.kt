package com.example.bics.ui.user.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.bics.R
import com.example.bics.ui.user.FormScreen
import com.example.bics.ui.user.ThickButton

@Composable
fun SuccessScreen(
    title: String,
    message: String,
    onConfirmPressed: () -> Unit
) {
    FormScreen(
        title = title,
        enabled = true,
        fieldContent = {
            Image(
                painter = painterResource(R.drawable.success),
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(0.4f).aspectRatio(1f).clip(CircleShape)
            )
            Text(message, modifier = Modifier.weight(1f))
        },
        buttons = {
            ThickButton(text = stringResource(R.string.continue_text), onClick = onConfirmPressed)
        },
        showBackButton = true,
        onBackButtonPressed = onConfirmPressed
    )
}