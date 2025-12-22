package com.example.bics.ui.dispatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bics.R
import com.example.bics.data.dispatch.DispatchItem
import com.example.bics.ui.theme.Typography
import java.lang.String.format

@Composable
fun SelectedProductsList(
    products: List<DispatchItem>,
    viewOnly: Boolean,
    onAddButtonClick: () -> Unit = {},
    onQuantityDecrease: (String) -> Unit = {},
    onQuantityIncrease: (String) -> Unit = {},
    onQuantityChange: (String, String) -> Unit = {id, value -> },
    onRemove: () -> Unit = {},
    onDismiss: () -> Unit = {},
    show: Boolean = false,
    enabled: Boolean = true
) {

    if (show) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Remove item from list?")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                onRemove()
                                onDismiss()
                            }
                        ) {
                            Text("Confirm", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(350.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Products", style = Typography.titleMedium)
                if (!viewOnly){
                    Button(onClick = onAddButtonClick, enabled = enabled) {
                        Text("Add +")
                    }
                }
            }
            LazyColumn(Modifier
                .weight(1f)
                .nestedScroll(rememberNestedScrollInteropConnection())) {
                items(products.sortedBy { it.product.name }, { it.product.id }) { dispatchItem ->

                    Column(horizontalAlignment = Alignment.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(dispatchItem.product.name, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)

                            if (!viewOnly) QuantityInput(
                                dispatchItem = dispatchItem,
                                onQuantityDecrease = onQuantityDecrease,
                                onQuantityIncrease = onQuantityIncrease,
                                onQuantityChange = onQuantityChange,
                                enabled = enabled,
                                buttonSize = 50.dp
                            ) else Text(dispatchItem.quantity.toString())
                        }
                        Text(format(stringResource(R.string.balance_format), dispatchItem.product.price * dispatchItem.quantity))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total: ")
                Text(format(stringResource(R.string.balance_format, products.sumOf { it.product.price * it.quantity })))
            }
        }
    }
}

@Composable
fun QuantityInput(
    dispatchItem: DispatchItem,
    onQuantityDecrease: (String) -> Unit,
    onQuantityIncrease: (String) -> Unit,
    onQuantityChange: (String, String) -> Unit,
    buttonSize: Dp = 50.dp,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = dispatchItem.quantity.toString().replaceFirst(Regex("^0+"), ""),
        onValueChange = { onQuantityChange(dispatchItem.product.id, it.filter { c -> c.isDigit()}) },
        textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center),
        leadingIcon = {
            TextButton(
                onClick = {
                    onQuantityDecrease(dispatchItem.product.id)
                },
                enabled = enabled,
                modifier = Modifier.size(buttonSize),
                contentPadding = PaddingValues(2.dp)
            ) {
                Text("-", style = Typography.titleMedium)
            }
        },
        trailingIcon = {
            TextButton(
                onClick = { onQuantityIncrease(dispatchItem.product.id) },
                enabled = enabled,
                modifier = Modifier.size(buttonSize),
                contentPadding = PaddingValues(2.dp)
            ) {
                Text("+", style = Typography.titleMedium)
            }
        },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = enabled,
        singleLine = true,
        modifier = Modifier
            .padding(4.dp)
            .width(buttonSize * 3)
            .onFocusChanged {
                if (dispatchItem.quantity == 0L) onQuantityDecrease(dispatchItem.product.id)
            }
    )
}