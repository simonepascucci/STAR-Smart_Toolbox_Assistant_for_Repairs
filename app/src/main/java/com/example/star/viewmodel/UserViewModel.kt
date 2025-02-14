package com.example.star.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.model.UserData
import com.example.star.model.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    private val _userData = MutableLiveData<UserData>()
    val userData : LiveData<UserData> = _userData

    fun getUserData(userEmail: String) {
        viewModelScope.launch {
            val userResult = userRepository.fetchUserData(userEmail)
            _userData.postValue(userResult)
        }
    }

    fun addNewUser(email: String, username: String) {
        viewModelScope.launch {
            userRepository.addNewUser(email, username)
        }
    }
}