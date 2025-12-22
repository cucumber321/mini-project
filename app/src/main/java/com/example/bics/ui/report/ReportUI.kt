package com.example.bics.ui.report

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bics.data.report.ReportComponent
import com.example.bics.data.report.ReportContent
import com.example.bics.data.report.ReportSortType
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import java.lang.String.format
import java.time.LocalDate
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Report(
    onBack: () -> Unit,
    viewModel: ReportViewModel = viewModel(factory = DispatchViewModelProvider.Factory),
) {

    val year by viewModel.year.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val some by viewModel.report.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(text = "Dispatch Summary Report")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        },
        bottomBar= {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DownloadButton(viewModel)
                }
            }
        }

    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                YearDropdownPicker(year) { selected ->
                    viewModel.setYear(selected)
                }
                SortPicker(sortType) {
                    viewModel.changeSort(it)
                }
            }

            Text(
                text = "Dispatch Summary Report $year",
                modifier = Modifier.padding(vertical = 10.dp)
            )
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

            LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                items(some) { reportItem ->
                    ReportTable(
                        reportComponent = reportItem
                    )
                }
            }
        }
    }
}



@SuppressLint("DefaultLocale")
@Composable
fun ReportTable(
    reportComponent: ReportComponent
){
    var showAll by remember { mutableStateOf(false) }

    Card(
        onClick = {showAll = !showAll},
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    ){
        Text(text = reportComponent.month.trimIndent(), modifier = Modifier.padding(8.dp))

        HorizontalDivider(thickness = 1.dp)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically, modifier =  Modifier.padding(8.dp)) {
            if(reportComponent.reportContent.isNotEmpty()){
                Text(
                    text = "Product",
                    modifier = Modifier.weight(1.5f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Quantity",
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "Subtotal (RM)",
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }else{
                Text(
                    text="No dispatch in this month!"
                )
                return@Card
            }
        }
        HorizontalDivider(thickness = 1.dp)

        val contents = if (showAll) reportComponent.reportContent
        else {
            val others = if (reportComponent.reportContent.size > 3) reportComponent.reportContent.subList(3, reportComponent.reportContent.size) else emptyList()
            reportComponent.reportContent.take(3) + ReportContent(
                others.sumOf { it.quantity },
                "Other Products",
                others.sumOf { it.subtotal })
        }

        for (content in contents) {
            TableRow(content.name, content.quantity, content.subtotal)
        }
        HorizontalDivider(thickness = 1.dp)
        TableRow(
            "Total",
            reportComponent.reportContent.sumOf { it.quantity },
            reportComponent.reportContent.sumOf { it.subtotal }
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TableRow(
    name: String,
    quantity: Long,
    subtotal: Double
) {
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = name,
                modifier = Modifier.weight(1.5f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis

            )
            Text(
                text = quantity.toString(),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis

            )
            Text(
                text = format("%,.2f", subtotal),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis

            )
        }
    }
}

@Composable
fun YearDropdownPicker(
    year: Int,
    startYear: Int = 2020,
    endYear: Int = LocalDate.now().year,
    onSelected: (Int) -> Unit,
) {
    val years = (endYear downTo startYear).toList()
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .border(
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ){
        TextButton(onClick = { expanded = true }, modifier = Modifier.width(100.dp)) {
            Text(year.toString())
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(100.dp)
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text("$year") },
                    onClick = {
                        onSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun SortPicker(
    sortType: ReportSortType,
    onSelected: (ReportSortType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .border(
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ){
        TextButton(onClick = { expanded = true }, modifier = Modifier.width(100.dp)) {
            Text(sortType.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(100.dp)
        ) {
            ReportSortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DownloadButton(
    viewModel: ReportViewModel
) {
    val context = LocalContext.current

    Button(onClick = {
        viewModel.generatePdf(context)
    }) {
        Text("Download PDF")
    }
}

