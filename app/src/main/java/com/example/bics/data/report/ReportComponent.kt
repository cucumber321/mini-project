package com.example.bics.data.report

data class ReportComponent(
    val month:String,
    val reportContent:List<ReportContent>
)

data class ReportContent(
    val quantity:Long = 0,
    val name:String = "",
    val subtotal:Double = 0.0,
)