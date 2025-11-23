package com.example.bics.data.repository.profile

import android.net.Uri
import androidx.core.net.toUri
import com.example.bics.data.user.FirestoreUserField
import com.example.bics.data.user.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirestoreProfileRepository: ProfileRepository {
    private val firestore = Firebase.firestore.collection(FirestoreUserField.Collection.key)

    private var storageRef = Firebase.storage.reference

    private var snapshotListener: ListenerRegistration? = null
    private val _profile = MutableStateFlow(UserProfile(""))

    private val profile = _profile.asStateFlow()

    override fun getUserStream(): StateFlow<UserProfile> {
        return profile
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
        } else{
            firestore.document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists())
                    updateProfileFromDoc(doc)
                else {
                    insertUser(UserProfile(uid))
                }
                _profile.update { it.copy(uid = uid, email = email) }
                startObserveUser()
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
                    FirestoreUserField.Balance.key to profile.balance
                    )
            )
    }

    override suspend fun getUser(uid: String): UserProfile {
        if (uid.isEmpty()) return UserProfile()
        val doc = firestore.document(uid).get().await()
        return UserProfile(
            uid = uid,
            username = doc.getString(FirestoreUserField.Username.key)?: FirestoreUserField.Username.default as String,
            profilePictureUri = doc.getString(FirestoreUserField.ProfilePicture.key)?.toUri() ?: FirestoreUserField.ProfilePicture.default as Uri,
            balance = doc.getDouble(FirestoreUserField.Balance.key) ?: FirestoreUserField.Balance.default as Double
        )
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
                balance = doc.getDouble(FirestoreUserField.Balance.key) ?: FirestoreUserField.Balance.default as Double,
                isAdmin = doc.getBoolean("isAdmin") ?: false
            )
        }
    }
}

