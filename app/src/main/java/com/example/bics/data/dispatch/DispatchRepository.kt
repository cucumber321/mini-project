package com.example.bics.data.dispatch

import androidx.lifecycle.SavedStateHandle
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.product.data.Product
import com.example.bics.data.report.ReportContent
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import okhttp3.internal.format
import java.time.LocalDate
import java.time.ZoneId

class DispatchRepository(private val profileRepository: ProfileRepository, private val productRepository: ProductRepository) {

    val state = mutableListOf<SavedStateHandle>()
    val firestore = Firebase.firestore.collection("dispatch")
    val productsFirestore = Firebase.firestore.collection("Products")
    var lastDoc: DocumentSnapshot? = null
    lateinit var query: Query
    var endReached = false

    suspend fun getDispatches(startDate: Timestamp?, endDate: Timestamp?): List<Dispatch> {
        val nextDay = endDate?.toInstant()?.plusSeconds(24 * 60 * 60)?.let { Timestamp(it) }
        query = firestore
            .orderBy("order_date", Query.Direction.DESCENDING)
            .limit(20)

        nextDay?.let {
            query = query.whereLessThan("order_date", it)
        }
        startDate?.let {
            query = query.whereGreaterThanOrEqualTo("order_date", it)
        }
        val docs = query.get().await()
        lastDoc = docs.lastOrNull()
        endReached = false

        return docs.mapNotNull { doc ->
            if (doc.id == "Counter") null
            else docToDispatch(doc)
        }
    }

    suspend fun loadMore(): List<Dispatch> {
        if (endReached) return emptyList()
        val docs = (lastDoc?.let { query.startAfter(it) } ?: query).get().await()

        if (docs.documents.isNotEmpty()) lastDoc = docs.last()
        else endReached = true

        return docs.mapNotNull { doc ->
            if (doc.id == "Counter") null
            else docToDispatch(doc)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun docToDispatch(doc: DocumentSnapshot): Dispatch = Dispatch(
        id = doc.id,
        orderedBy = doc.getString("ordered_by") ?: "Unknown",
        dateCreated = doc.getTimestamp("order_date") ?: Timestamp.now(),
        createdBy = doc.getString("created_user_id") ?: "",
        lastModifiedBy = doc.getString("last_modified_user_id"),
        lastModifiedDate = doc.getTimestamp("last_modified_date"),
        items = (doc.get("products") as? Map<String, Map<String, Object>> ?: emptyMap())
            .map { entry ->
                DispatchItem(
                    product = Product(id = entry.key, price = (entry.value["price"] as? Number)?.toDouble() ?: 0.0),
                    quantity = entry.value["quantity"] as? Long ?: 0
                )
            }
    )

    suspend fun createDispatch(orderedBy: String, productList: List<DispatchItem>) {
        val doc = firestore.document("Counter").get().await()
        val lastId = doc.getLong("last_id") ?: 1
        doc.reference.set(mapOf("last_id" to lastId + 1))

        updateDispatch(
            Dispatch(
                id = format("D%03d", lastId),
                orderedBy = orderedBy,
                dateCreated = Timestamp.now(),
                createdBy = profileRepository.getUserStream().value.uid,
                items = productList,
            )
        )
    }

    suspend fun updateDispatch(dispatch: Dispatch) {
        val doc = firestore.document(dispatch.id)

        val dispatchMap = mutableMapOf(
            "created_user_id" to dispatch.createdBy,
            "order_date" to dispatch.dateCreated,
            "ordered_by" to dispatch.orderedBy,
            "products" to dispatch.items.associate { it.product.id to mapOf("price" to it.product.price, "quantity" to it.quantity) }
        )

        dispatch.lastModifiedBy?.let {
            dispatchMap["last_modified_user_id"] = it
        }
        dispatch.lastModifiedDate?.let {
            dispatchMap["last_modified_date"] = it
        }

        updateStockQuantity(dispatch.id, dispatch.items.associate { it.product.id to it.quantity })
        doc.set(dispatchMap)

    }

    suspend fun getDispatch(dispatchId: String): Dispatch {
        if (dispatchId.isBlank()) return Dispatch()
        val doc = firestore.document(dispatchId)

        val dispatch: Dispatch = docToDispatch(doc.get().await())

        val idToProduct = productRepository.getProducts(dispatch.items.map { it.product.id }).associateBy { it.id }
        return dispatch.copy(
            items = dispatch.items.map {
                it.copy(product = idToProduct.getOrDefault(it.product.id, Product(id = it.product.id, name = format("Unknown Product (%s)", it.product.id)))
                    .copy(price = it.product.price)
                )
            }
        )
    }

    suspend fun updateStockQuantity(dispatchId: String, after: Map<String, Long>) {
        @Suppress("UNCHECKED_CAST")
        val before = (firestore
            .document(dispatchId)
            .get().await()
            .get("products") as? Map<String, Map<String, Object>>
            ?:emptyMap())
            .mapValues { -(it.value["quantity"] as? Long ?: 0) }
            .toMutableMap()

        after.forEach { k, v ->
            before.merge(k, v, Long::plus)
        }


        val currentQuantity = productRepository.getProducts(before.keys).associate { it.id to it.quantity }

        for (chunk in before.entries.chunked(500)) {
            val batch = Firebase.firestore.batch()

            for (entry in chunk) {
                currentQuantity[entry.key]?.let { q ->
                    batch.update(
                        productsFirestore.document(entry.key),
                        mapOf("quantity" to q - entry.value)
                    )
                }
            }

            batch.commit()
        }
    }

    suspend fun deleteDispatch(dispatchId: String) {
        updateStockQuantity(dispatchId, emptyMap())
        firestore.document(dispatchId).delete()
    }

    suspend fun getAnnualDispatch(year: Int): List<List<ReportContent>> {
        val total = List(12) { mutableMapOf<String, ReportContent>() }
        val startDate = Timestamp(LocalDate
            .of(year, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant())
        val endDate = Timestamp(LocalDate
            .of(year + 1, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant())

        val dispatchDocs = firestore
            .whereGreaterThanOrEqualTo("order_date", startDate)
            .whereLessThanOrEqualTo("order_date", endDate)
            .whereLessThanOrEqualTo("order_date", endDate)
            .get().await()

        for (doc in dispatchDocs) {
            val date = doc.getTimestamp("date_created")?: Timestamp.now()
            val monthIndex = date.toInstant().atZone(ZoneId.systemDefault()).month.value - 1

            @Suppress("UNCHECKED_CAST")
            val products = doc.get("products") as? Map<String, Map<String, Object>> ?: emptyMap()

            for ((id, map) in products) {
                val q = map["quantity"] as? Long ?: 0
                total[monthIndex].merge(
                    id,
                    ReportContent(
                        name = id,
                        quantity = q,
                        subtotal = ((map["price"] as? Number)?.toDouble() ?: 0.0) * q,
                    )
                ) { a, b ->
                    a.copy(quantity = a.quantity + b.quantity, subtotal = a.subtotal + b.subtotal)
                }
            }
        }

        return total.map { m ->  m.values.toList() }
    }
}