package com.example.bics.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bics.R
import com.example.bics.data.schedule.Shift
import com.example.bics.ui.theme.Typography
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
    selectedDate: String,
    onDateSelected: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = LocalDate.now(
        ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
    var showPicker by remember { mutableStateOf(false) }

    Button(
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(8.dp),
        onClick = {showPicker = true}
    ) {
        Text(text = selectedDate)
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
                        onDateSelected(datePickerState.selectedDateMillis)
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

@Composable
fun ShiftBox(shift: Shift, onClick: () -> Unit) {
    val pattern = DateTimeFormatter.ofPattern("HH:mm")
    Button(onClick = onClick, shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Column() {
                Text(text = shift
                    .startTime
                    .toInstant()
                    .atZone(ZoneOffset.UTC)
                    .toLocalTime()
                    .format(pattern)
                )
                Text(text = shift
                    .endTime
                    .toInstant()
                    .atZone(ZoneOffset.UTC)
                    .toLocalTime()
                    .format(pattern)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(text = shift.title, style = Typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = shift.usersAssigned.joinToString(separator = ", "), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}