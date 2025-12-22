package com.example.bics.ui.report

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.report.ReportComponent
import com.example.bics.data.report.ReportSortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ReportViewModel(private val dispatchRepository: DispatchRepository, private val productRepository: ProductRepository) : ViewModel() {


    var report = MutableStateFlow<List<ReportComponent>>(emptyList())
        private set

    private val _sortType = MutableStateFlow(ReportSortType.Subtotal)

    private val _year = MutableStateFlow(LocalDate.now().year)
    val year = _year.asStateFlow()
    val sortType = _sortType.asStateFlow()

    init {
        _year.onEach {
            fetchProductDetails()
        }.launchIn(viewModelScope)
    }

    fun setYear(selectedYear: Int) {
        _year.update { selectedYear }
    }

    private suspend fun fetchProductDetails() {
        val qty = dispatchRepository.getAnnualDispatch(_year.value)
        val productIds = qty.flatMap { list -> list.map { it.name } }.distinct()
        val idToName = productRepository.getProducts(productIds).associate { it.id to it.name }
        val components = qty.mapIndexed { i, contents ->
            ReportComponent(
                monthNumberToName(i),
                contents.map {
                    it.copy(name = idToName[it.name] ?: format("Unknown Product (%s)", it.name))
                }
            )
        }

        report.update { it ->
            components.map { contents ->
                contents.copy(
                    reportContent = when(_sortType.value) {
                        ReportSortType.Name -> contents.reportContent.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                        ReportSortType.Quantity -> contents.reportContent.sortedByDescending { it.quantity }
                        ReportSortType.Subtotal -> contents.reportContent.sortedByDescending { it.subtotal }
                    }
                )
            }
        }
    }

    fun monthNumberToName(n: Int): String {
        return listOf(
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        ).getOrElse(n) { "Invalid" }
    }

    @SuppressLint("DefaultLocale")
    fun generatePdf(context: Context) {
        if (report.value.all { it.reportContent.isEmpty() }) {
            Toast.makeText(
                context,
                "No Dispatches, Cannot Create Report",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val pdf = PdfDocument()
        val paint = Paint()
        val linePaint = Paint().apply {
            strokeWidth = 1.5f
            color = Color.GRAY
        }
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        var pageCounter=0

        for ((pageIndex, r) in report.value.withIndex()) {
            if(r.reportContent.isEmpty()) {
                pageCounter++
                continue
            }
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas
            var y = 60f

            paint.textSize = 22f
            paint.isFakeBoldText = true
            canvas.drawText("BICS Sdn Bhd", 50f, y, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("Generated: $date", 400f, y, paint)

            y += 30f

            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("Dispatch Summary - ${r.month}", 50f, y, paint)

            y += 20f
            canvas.drawLine(50f, y, 545f, y, linePaint)   // underline

            y += 25f

            paint.textSize = 14f
            paint.isFakeBoldText = true

            canvas.drawText("Product", 50f, y, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Qty", 350f, y, paint)
            canvas.drawText("Subtotal (RM)", 530f, y, paint)
            paint.textAlign = Paint.Align.LEFT

            y += 10f
            canvas.drawLine(50f, y, 545f, y, linePaint)

            y += 20f

            paint.isFakeBoldText = false

            var totalAmount = 0.0

            for (item in r.reportContent) {
                if (item.name == "Total") continue
                canvas.drawText(item.name, 50f, y, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(item.quantity.toString(), 350f, y, paint)
                canvas.drawText(String.format("%,.2f", item.subtotal), 530f, y, paint)
                paint.textAlign = Paint.Align.LEFT
                y += 20f
                totalAmount += item.subtotal
            }

            y += 10f
            canvas.drawLine(50f, y, 545f, y, linePaint)

            y += 30f

            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("Total: RM ${String.format("%,.2f", totalAmount)}", 50f, y, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("Page ${pageIndex + 1 - pageCounter}", 270f, 820f, paint)

            pdf.finishPage(page)
        }

        savePdfToDocuments(context, pdf, format("Dispatch_Report_%s.pdf", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))))
    }

    private fun savePdfToDocuments(context: Context, pdf: PdfDocument, filename: String) {
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/DispatchReports"
            )
        }

        try {
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
            val stream = resolver.openOutputStream(uri!!) ?: return
            pdf.writeTo(stream)
            stream.close()

            Toast.makeText(context, "PDF saved in Documents/DispatchReports", Toast.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            Toast.makeText(context, "PDF Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            pdf.close()
        }
    }

    fun changeSort(type: ReportSortType) {
        _sortType.update { type }
        report.update { r ->
            r.map { contents ->
                contents.copy(
                    reportContent = when(type) {
                        ReportSortType.Name -> contents.reportContent.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                        ReportSortType.Quantity -> contents.reportContent.sortedByDescending { it.quantity }
                        ReportSortType.Subtotal -> contents.reportContent.sortedByDescending { it.subtotal }
                    }
                )

            }
        }
    }
}
