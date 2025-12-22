package com.example.bics.data.repository.profile

import android.net.Uri
import androidx.core.net.toUri
import com.example.bics.data.user.FirestoreUserField
import com.example.bics.data.user.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import okhttp3.internal.format

class FirestoreProfileRepository: ProfileRepository {
    private val firestore = Firebase.firestore.collection(FirestoreUserField.Collection.key)
    private val userMapCollection = Firebase.firestore.collection("user_map")

    private var storageRef = Firebase.storage.reference

    private var snapshotListener: ListenerRegistration? = null
    private val _profile = MutableStateFlow(UserProfile(""))

    private val profile = _profile.asStateFlow()

    override fun getUserStream(): StateFlow<UserProfile> {
        return profile
    }

    override suspend fun getUserList(): List<UserProfile> {
        return firestore.orderBy(FirestoreUserField.Username.key).get().await().map(::docToUserProfile)
    }
    override fun updateUser(key: FirestoreUserField, value: Any) {
        firestore
            .document(_profile.value.uid)
            .update(
                mapOf(
                    key.key to value,
                )
            )
    }

    override fun deleteUser() {
        firestore.document(_profile.value.uid).delete()
    }

    override suspend fun updateProfilePicture(selectedUri: Uri) {
        if (selectedUri != profile.value.profilePictureUri) {
            _profile.update {
                it.copy(profilePictureUri = selectedUri)
            }
            val profileRef = storageRef.child(_profile.value.uid)
            profileRef.putFile(selectedUri)
                .addOnSuccessListener {
                    profileRef.downloadUrl.addOnSuccessListener { uri ->
                        updateUser(FirestoreUserField.ProfilePicture, uri)
                    }
                }
        }
    }

    override fun changeUser(uid: String, email: String) {
        stopObserveUser()
        if (uid.isEmpty()) {
            _profile.update { UserProfile() }
        } else {
            userMapCollection.document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val userID = doc.getString("user_id")?: ""
                    firestore.document(userID).get().addOnSuccessListener { doc2 ->
                        if (doc2.exists())
                            updateProfileFromDoc(doc2)
                        else {
                            insertUser(UserProfile(userID))
                        }
                        _profile.update { it.copy(uid = userID, email = email) }
                        startObserveUser()
                    }
                }
            }
        }
    }

    override fun insertUser(profile: UserProfile) {
        firestore
            .document(profile.uid)
            .set(
                hashMapOf(
                    FirestoreUserField.Username.key to profile.username,
                    FirestoreUserField.ProfilePicture.key to profile.profilePictureUri,
                    )
            )
    }
    fun docToUserProfile(doc: DocumentSnapshot): UserProfile {
        return UserProfile(
            uid = doc.id,
            username = doc.getString(FirestoreUserField.Username.key)?: FirestoreUserField.Username.default as String,
            profilePictureUri = doc.getString(FirestoreUserField.ProfilePicture.key)?.toUri() ?: FirestoreUserField.ProfilePicture.default as Uri,
        )
    }

    override suspend fun getUser(uid: String): UserProfile {
        val doc = firestore.document(uid).get().await()
        return docToUserProfile(doc)
    }

    override suspend fun getUsers(uids: List<String>): List<UserProfile> {
        return uids.chunked(10).flatMap {
            firestore.whereIn(FieldPath.documentId(), it).get().await().map(::docToUserProfile)
        }
    }

    override suspend fun addUuidToMap(uid: String): String {
        val doc = firestore.document(FirestoreUserField.Counter.key).get().await()
        val displayID = (doc.getLong(FirestoreUserField.LastID.key) ?: FirestoreUserField.DisplayID.default as Long)
        userMapCollection.document(uid).set(mapOf("user_id" to format("U%03d", displayID)))
        firestore.document(FirestoreUserField.Counter.key).update(mapOf(FirestoreUserField.LastID.key to displayID + 1))
        return format("U%03d", displayID)
    }

    private fun startObserveUser() {
        snapshotListener = firestore.document(_profile.value.uid).addSnapshotListener {
            doc, e ->
            updateProfileFromDoc(doc)
        }
    }

    private fun stopObserveUser() {
        snapshotListener?.remove()
    }

    private fun updateProfileFromDoc(doc: DocumentSnapshot?) {
        if (doc == null || !doc.exists()) return
        _profile.update {
            it.copy(
                username = doc.getString(FirestoreUserField.Username.key)?: FirestoreUserField.Username.default as String,
                profilePictureUri = doc.getString(FirestoreUserField.ProfilePicture.key)?.toUri() ?: FirestoreUserField.ProfilePicture.default as Uri,
            )
        }
    }
}

