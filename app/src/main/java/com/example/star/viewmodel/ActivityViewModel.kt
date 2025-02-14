package com.example.star.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.model.Activity
import com.example.star.model.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private val activityRepository: ActivityRepository = ActivityRepository()

    private val _activityData = MutableLiveData<MutableList<Activity>>()
    val activityData : LiveData<MutableList<Activity>> = _activityData

    private val _collaborationsData = MutableLiveData<MutableList<Activity>>()
    val collaborationsData : LiveData<MutableList<Activity>> = _collaborationsData

    private val _showForm = MutableLiveData<Boolean>()
    val showForm : LiveData<Boolean> = _showForm

    private val _selectedActivity = MutableLiveData<Activity?>()
    val selectedActivity : LiveData<Activity?> = _selectedActivity

    val addCollaboratorResult = MutableStateFlow<AddCollaboratorResult>(AddCollaboratorResult.Idle)

    fun getUserActivities(email: String) {
        viewModelScope.launch {
            val activities = activityRepository.fetchUserActivities(email)
            _activityData.postValue(activities)
        }
    }

    fun getUserCollaborations(email: String) {
        viewModelScope.launch {
            val collaborations = activityRepository.fetchUserCollaborations(email)
            _collaborationsData.postValue(collaborations)
        }
    }

    fun addNewActivity(name: String, category: String, author: String, collaborators: MutableList<String>, status: String) {
        viewModelScope.launch {
            activityRepository.addActivity(
                name = name,
                category = category,
                author = author,
                collaborators = collaborators,
                status = status
            )
        }
    }

    fun enableForm() {
        _showForm.postValue(true)
    }
    fun disableForm() {
        _showForm.postValue(false)
    }

    fun selectActivity(activity: Activity) {
        viewModelScope.launch {
            _selectedActivity.postValue(activityRepository.selectActivity(activity.name))
        }
    }

    fun updateActivity(activityId: String, activityField: String, activityValue: Any) {
        viewModelScope.launch {
            activityRepository.updateActivity(activityId, activityField, activityValue)
            _selectedActivity.postValue(activityRepository.selectActivity(activityId))
        }
    }

    fun addCollaborator(activityId: String, collaborator: String) {
        viewModelScope.launch {
            addCollaboratorResult.value = AddCollaboratorResult.Loading
            val result = activityRepository.addCollaborator(activityId, collaborator)
            _selectedActivity.postValue(activityRepository.selectActivity(activityId))
            addCollaboratorResult.value = if (result) {
                AddCollaboratorResult.Success
            } else {
                AddCollaboratorResult.Failure
            }
        }
    }

    fun removeCollaboration(activityId: String, collaborator: String) {
        viewModelScope.launch {
            activityRepository.removeCollaboration(activityId, collaborator)
            _selectedActivity.postValue(activityRepository.selectActivity(activityId))
        }
    }

    fun setCompleted(activityId: String) {
        viewModelScope.launch {
            activityRepository.setCompleted(activityId)
            _selectedActivity.postValue(activityRepository.selectActivity(activityId))
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            activityRepository.deleteActivity(activityId)
            _selectedActivity.postValue(null)
        }
    }

}

sealed class AddCollaboratorResult {
    data object Success : AddCollaboratorResult()
    data object Failure : AddCollaboratorResult()
    data object Loading : AddCollaboratorResult()
    data object Idle : AddCollaboratorResult()
}