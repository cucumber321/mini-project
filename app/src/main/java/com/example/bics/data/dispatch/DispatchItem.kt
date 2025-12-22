package com.example.bics.data.dispatch

import android.os.Parcelable
import com.example.product.data.Product
import kotlinx.parcelize.Parcelize
import okhttp3.internal.toLongOrDefault
import kotlin.math.max
import kotlin.math.min

@Parcelize
data class DispatchItem(
    val product: Product,
    val quantity: Long
): Parcelable {
    fun increment(taken: Map<String, Long>): DispatchItem = this.copy(quantity = min(quantity + 1, product.quantity + taken.getOrDefault(this.product.id, 0)))
    fun decrement(): DispatchItem = this.copy(quantity = max(quantity - 1, 0))
    fun onChange(value: String, taken: Map<String, Long>): DispatchItem {
        val q = value.toLongOrDefault(0)

        return this.copy(quantity = q.coerceIn(0, product.quantity + taken.getOrDefault(this.product.id, 0)))
    }
}