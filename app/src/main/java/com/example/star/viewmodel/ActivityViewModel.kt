package com.example.star.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.model.Activity
import com.example.star.model.ActivityRepository
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private val activityRepository: ActivityRepository = ActivityRepository()

    private val _activityData = MutableLiveData<MutableList<Activity>>()
    val activityData : LiveData<MutableList<Activity>> = _activityData

    private val _showForm = MutableLiveData<Boolean>()
    val showForm : LiveData<Boolean> = _showForm


    fun getUserActivities(email: String) {
        viewModelScope.launch {
            val activities = activityRepository.fetchUserActivities(email)
            _activityData.postValue(activities)
        }
    }

    fun addNewActivity(name: String, category: String, author: String, collaborators: MutableList<String>, status: String, createdAt: String) {
        viewModelScope.launch {
            activityRepository.addActivity(
                name = name,
                category = category,
                author = author,
                collaborators = collaborators,
                status = status,
                createdAt = createdAt
            )
        }
    }

    fun enableForm() {
        _showForm.postValue(true)
    }
    fun disableForm() {
        _showForm.postValue(false)
    }

}