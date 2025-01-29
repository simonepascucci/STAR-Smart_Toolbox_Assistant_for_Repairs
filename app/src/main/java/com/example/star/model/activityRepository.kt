package com.example.star.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class Activity (
    var author: String = "",
    var category: String = "",
    var collaborators: MutableList<String> = mutableListOf(),
    var createdAt: String,
    var name: String = "",
    var status: String = ""
){
    constructor() : this("", "", mutableListOf(), "", "", "")
}

class ActivityRepository {
    private val db = Firebase.firestore

    suspend fun fetchUserActivities(email: String): MutableList<Activity> {
        val activities = mutableListOf<Activity>()
        try {
            val querySnapshot = db.collection("activities")
                .whereEqualTo("author", email)
                .get()
                .await()

            querySnapshot.documents.forEach { document ->
                val act = document.toObject(Activity::class.java)
                if (act != null) {
                    activities.add(act)
                } else {
                    Log.w("Firestore", "Activity object is null for document: ${document.id}")
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching activities", e)
        }
        return activities
    }


    suspend fun addActivity(name: String, category: String, author: String, collaborators: MutableList<String>, status: String, createdAt: String) {
        try {
            db.collection("activities").add(Activity(author, category, collaborators, createdAt, name, status)).await()
        }catch (e: Exception){
            Log.e("ActivityRepository", "Error adding activity $createdAt", e)
        }
    }
}