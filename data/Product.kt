package com.example.product.data

import android.net.Uri

data class Product (
    val productID: String="",
    val productName: String="",
    val productDescription: String="",
    val productPrice: Double=0.0,
    val productQuantity: Int=0,
    val productImage: Uri?=null
)
