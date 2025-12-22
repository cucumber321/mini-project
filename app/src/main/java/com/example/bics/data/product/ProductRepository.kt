package com.example.bics.data.product

import android.net.Uri
import com.example.product.data.Product
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
//import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import okhttp3.internal.format
import kotlin.collections.chunked

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Upload image to Firebase Storage
    suspend fun uploadImage(productId: String, imageUri: Uri, onFailure: (String) -> Unit): String {
        try {
            val ref = storage.reference.child("product_images/$productId")
            ref.putFile(imageUri).await()
            return ref.downloadUrl.await().toString()
        } catch (_: Exception) {
            onFailure("Image Upload Failed")
            return ""
        }
    }

    // Auto-generate Product ID (P001, P002, ...)
    suspend fun generateId(onFailure: () -> Unit): String {
        try {
            val doc = firestore.collection("Products").document("Counter").get().await()
            val lastIdNumber = doc.getLong("last_id") ?: 1

            val newId = format("P%03d", lastIdNumber)
            firestore.collection("Products").document("Counter").set(mapOf("last_id" to lastIdNumber + 1))
            return newId
        } catch(_: Exception) {
            onFailure()
            return ""
        }
    }


    // Save product to Firestore
    fun saveProduct(
        id: String,
        name: String,
        price: Double,
        qty: Long,
        desc: String,
        imageUrl: String,
        onToast: (String) -> Unit,
    ) {
        val data = hashMapOf(
            "id" to id,
            "name" to name,
            "price" to price,
            "quantity" to qty,
            "description" to desc,
            "imageUrl" to imageUrl
        )

        firestore.collection("Products")
            .document(id)
            .set(data)
            .addOnSuccessListener {
                onToast("Product saved: $id")
            }
            .addOnFailureListener {
                onToast("Error: ${it.message}")
            }
    }

    suspend fun getProduct(productId: String): Product {
        val doc = firestore.collection("Products").document(productId).get().await()
        return docToProduct(doc)
    }

    suspend fun getProducts(ids: Collection<String>): List<Product> {
        return ids.chunked(10).flatMap {
            firestore.collection("Products").whereIn(FieldPath.documentId(), it).get().await().map(::docToProduct)
        }
    }

    fun docToProduct(doc: DocumentSnapshot) = Product(
        id = doc.id,
        name = doc.getString("name") ?: "",
        price = doc.getDouble("price") ?: 0.0,
        quantity = doc.getLong("quantity") ?: 0,
        imageUrl = doc.getString("imageUrl") ?: "",
        description = doc.getString("description") ?: ""
    )

    suspend fun getAllProducts(): List<Product> {
        return firestore.collection("Products").get().await().documents.mapNotNull { if (it.id == "Counter") null else docToProduct(it) }
    }
}