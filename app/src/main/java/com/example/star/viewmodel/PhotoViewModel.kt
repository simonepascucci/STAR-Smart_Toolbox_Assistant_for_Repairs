package com.example.star.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.model.PhotoRepository
import com.example.star.model.room.BitmapConverter
import com.example.star.model.room.PhotoDao
import com.example.star.model.room.PhotoEntity
import kotlinx.coroutines.launch

class PhotoViewModel(context: Context) : ViewModel(){

    private val photoRepository: PhotoRepository = PhotoRepository(context)

    private val _photos = MutableLiveData<List<PhotoEntity>>()
    val photos: LiveData<List<PhotoEntity>> = _photos


    @RequiresApi(Build.VERSION_CODES.R)
    fun insertPhoto(bitmap: Bitmap, activity: String){
        viewModelScope.launch {
            val byteArray = BitmapConverter().fromBitmap(bitmap)
            photoRepository.insertPhoto(PhotoEntity(image = byteArray, activity = activity))
            fetchPhotos(activity)
        }
    }

    fun fetchPhotos(activity: String){
        viewModelScope.launch {
            _photos.value = photoRepository.getActivityPhotos(activity)
        }
    }

    fun updatePhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            photoRepository.updatePhoto(photo)
            fetchPhotos(photo.activity!!)
        }
    }

    fun deleteAllPhotos() {
        viewModelScope.launch {
            photoRepository.deleteAllPhotos()
        }
    }

    fun deletePhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            photoRepository.deletePhoto(photo)
            fetchPhotos(photo.activity!!)
        }
    }
}