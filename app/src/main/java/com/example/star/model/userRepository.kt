package com.example.star.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class UserData(
    var email: String = "",
    var username: String = ""
)

class UserRepository {

    private val db = Firebase.firestore
    private val tag = "UserRepository"

    suspend fun fetchUserData(userEmail: String): UserData {
        return try {
            val querySnapshot = db.collection("users").whereEqualTo("email", userEmail).get().await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first() // Get the first matching document
                val userData = document.toObject(UserData::class.java) // Convert to UserData
                userData ?: UserData("", "") // Return extracted data or empty UserData
            } else {
                Log.d(tag, "No user found with email: $userEmail")
                UserData("", "") // Return empty user if not found
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user data", e)
            UserData("", "") // Return empty user on error
        }
    }

    suspend fun addNewUser(email: String, username: String) {
        db.collection("users").add(UserData(email, username)).await()
    }
}
