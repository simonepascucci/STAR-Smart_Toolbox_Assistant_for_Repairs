package com.example.star.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.model.api.ElapsedTimeModel
import com.example.star.model.api.NetworkResponse
import com.example.star.model.api.RetrofitInstance
import com.example.star.model.api.Timestamps
import kotlinx.coroutines.launch

class ElapsedTimeViewModel : ViewModel() {

    private val elapsedTimeAPI = RetrofitInstance.elapsedTimeAPI
    private val _elapsedTimeResult = MutableLiveData<NetworkResponse<ElapsedTimeModel>?>()
    val elapsedTimeResult: MutableLiveData<NetworkResponse<ElapsedTimeModel>?> = _elapsedTimeResult

    fun fetchData(timestamp1: Long, timestamp2: Long) {
        _elapsedTimeResult.value = NetworkResponse.Loading
        try {
            viewModelScope.launch {
                val response = elapsedTimeAPI.getElapsedTime(Timestamps(timestamp1, timestamp2))
                if (response.isSuccessful) {
                    response.body()?.let {
                        _elapsedTimeResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _elapsedTimeResult.value = NetworkResponse.Error(response.message())
                }
            }
        }catch (e: Exception){
            _elapsedTimeResult.value = NetworkResponse.Error("Error fetching elapsed time: $e")
        }

    }
    fun resetElapsedTime() {
        _elapsedTimeResult.value = null
    }

}