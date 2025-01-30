package com.example.star.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class Activity(
    var author: String = "",
    var category: String = "",
    var collaborators: MutableList<String> = mutableListOf(),
    var completedAt: Timestamp?,
    var createdAt: Timestamp = Timestamp.now(),
    var name: String = "",
    var status: String = ""
){
    constructor() : this("", "", mutableListOf(), null, Timestamp.now(), "", "")
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


    suspend fun addActivity(name: String, category: String, author: String, collaborators: MutableList<String>, status: String) {
        try {
            db.collection("activities").document(name).set(Activity(
                author = author,
                category = category,
                collaborators = collaborators,
                completedAt = null,
                createdAt = Timestamp.now(),
                name = name,
                status = status
            )).await()
        }catch (e: Exception){
            Log.e("ActivityRepository", "Error adding activity ${Timestamp.now()}", e)
        }
    }

    suspend fun updateActivity(activityId: String, activityField: String, activityValue: Any) {
        try {
            db.collection("activities").document(activityId).update(activityField, activityValue).await()
        }catch (e: Exception){
            Log.e("ActivityRepository", "Error updating activity $activityId", e)
        }
    }

    suspend fun selectActivity(activityId: String) : Activity{
        return try {
            val snapshot = db.collection("activities").document(activityId).get().await()
            val result = snapshot.toObject(Activity::class.java)!!
            result
        }catch (e: Exception){
            Log.e("ActivityRepository", "Error selecting activity $activityId", e)
            Activity()
        }
    }

    suspend fun setCompleted(activityId: String) {
        try {
            db.collection("activities").document(activityId).update("completedAt", Timestamp.now()).await()
            db.collection("activities").document(activityId).update("status", "COMPLETED").await()
        }catch (e: Exception){
            Log.e("ActivityRepository", "Error setting completed activity $activityId", e)
        }
    }
}