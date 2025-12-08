package com.example.bics.ui.schedule

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bics.R
import com.example.bics.data.schedule.Shift
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.UserProfile
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.ProfilePicture
import okhttp3.internal.format
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    initialDateMilli: Long? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    onDateSelected: (Instant?) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMilli ?: LocalDate
            .now()
            .atStartOfDay()
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
    var showPicker by remember { mutableStateOf(false) }

    OutlinedButton(
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(8.dp),
        onClick = {showPicker = true},
        enabled = enabled,
        colors =
            if (!isError)
                ButtonDefaults.outlinedButtonColors()
            else ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
        border = if (isError) BorderStroke(1.dp, color = MaterialTheme.colorScheme.error) else ButtonDefaults.outlinedButtonBorder(enabled)
    ) {
        Text(text =
            if (datePickerState.selectedDateMillis == null) "-- --- ----" else
            Instant
                .ofEpochMilli(datePickerState.selectedDateMillis!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .formatDate(),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(R.drawable.calendar),
            contentDescription = "Calendar",
            modifier = Modifier.size(24.dp)
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = {showPicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(
                            datePickerState.selectedDateMillis?.let {
                                Instant
                                    .ofEpochMilli(it)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                            }
                        )
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
    wrapper: FieldUiStateWrapper<Long>,
    error: (ErrorCode) -> ErrorCode = {it},
    enabled: Boolean = true
) {
    val uiState by wrapper.uiState.collectAsState()
    val timePickerState = rememberTimePickerState(
        initialHour = (uiState.fieldInput / 3600).toInt(),
        initialMinute = ((uiState.fieldInput / 60) % 60).toInt()
    )
    var showPicker by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        OutlinedButton(
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(8.dp),
            onClick = {showPicker = true},
            enabled = enabled,
            colors =
                if (uiState.errorCode == ErrorCode.None)
                    ButtonDefaults.outlinedButtonColors()
                else ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
            border = if (uiState.errorCode != ErrorCode.None) BorderStroke(1.dp, color = MaterialTheme.colorScheme.error) else ButtonDefaults.outlinedButtonBorder(enabled)
        ) {
            Text(text = format("%02d:%02d", timePickerState.hour, timePickerState.minute))
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.clock),
                contentDescription = "Calendar",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = stringResource(error(uiState.errorCode).errorMessage),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = {showPicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        wrapper.onValueChanged(
                            ((timePickerState.hour * 60 + timePickerState.minute) * 60).toLong()
                        )
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            text =  {
                TimePicker(
                    state = timePickerState,
                )
            }
        )
    }
}

@Composable
fun ShiftBox(shift: Shift, onClick: () -> Unit) {
    Button(onClick = onClick, shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxSize()
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Text(text = shift
                    .startDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .formatTime(),
                    style = Typography.titleMedium
                )
                Text(text = shift
                    .endDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .formatTime(),
                    style = Typography.titleMedium
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(text = shift.title, style = Typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = shift.profiles.joinToString(separator = ", ") { it.username }, style = Typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun SelectUsersList(
    usersUiState: List<UserProfile>,
    title: String,
    modifier: Modifier = Modifier,
    errorCode: ErrorCode = ErrorCode.None,
    viewOnly: Boolean = false,
    enabled: Boolean = true,
    onRemoveUserClick: (String) -> Unit = {},
    onAddUserClick: () -> Unit = {}
) {

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)) {
            Text(text = title)
            if (!viewOnly) {
                Button(enabled = enabled, onClick = onAddUserClick) {
                    Text(text = "Add")
                }
            }
        }
        HorizontalDivider()
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(usersUiState) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (viewOnly) {
                        ProfilePicture(it.profilePictureUri, modifier = Modifier.size(48.dp))
                    } else {
                        SelectUserIcon(it, modifier = Modifier.size(48.dp)) {
                            if (enabled) onRemoveUserClick(it.uid)
                        }
                    }
                    Text(
                        it.username,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(96.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        HorizontalDivider()
        Text(
            text = stringResource(errorCode.errorMessage),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SelectUserIcon(profile: UserProfile, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier
            .clickable(onClick = onClick),
    ) {
        ProfilePicture(
            profile.profilePictureUri,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .border(2.dp, MaterialTheme.colorScheme.inverseOnSurface, CircleShape)
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxSize(0.25f)
                .background(MaterialTheme.colorScheme.onSurface)
                .border(2.dp, MaterialTheme.colorScheme.inverseOnSurface, CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun SelectUserRow(profile: UserProfile, chosen: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        ProfilePicture(profile.profilePictureUri, modifier = Modifier
            .clip(CircleShape)
            .padding(16.dp)
            .size(48.dp))
        Text(text = profile.username, modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .padding(8.dp)
            .size(24.dp)
            .border(1.dp, MaterialTheme.colorScheme.inverseOnSurface, CircleShape)) {
            if (chosen) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "Check",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SearchBar(value: String, onSearch: (String) -> Unit, modifier: Modifier = Modifier, placeholder: String = "") {
    OutlinedTextField(
        value = value,
        onValueChange = onSearch,
        shape = RoundedCornerShape(48.dp),
        placeholder = { Text(text = placeholder) },
        trailingIcon = {
            Icon(painter = painterResource(R.drawable.search), contentDescription = "Search", modifier = Modifier.size(24.dp))
        },
        maxLines = 1,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
fun CustomFloatingButton(@DrawableRes icon: Int, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)){
        FloatingActionButton (
            onClick = onClick,
            shape = CircleShape,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Icon",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CustomRadioButton(selected: Boolean, text: String, onClick: () -> Unit) {
    if (selected) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = onClick
        ) {
            Text(text=text)
        }
    } else {
        OutlinedButton(
            shape = RoundedCornerShape(8.dp),
            onClick = onClick
        ) {
            Text(text=text)
        }
    }
}

fun LocalDate.formatDate(): String = this.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
fun LocalTime.formatTime(): String = this.format(DateTimeFormatter.ofPattern("HH:mm"))







