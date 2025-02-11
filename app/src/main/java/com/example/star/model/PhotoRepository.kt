package com.example.star.model

import android.content.Context
import android.util.Log
import com.example.star.model.room.AppDatabase
import com.example.star.model.room.PhotoDao
import com.example.star.model.room.PhotoEntity

class PhotoRepository(context: Context) {

    private val photoDao: PhotoDao = AppDatabase.getDatabase(context).photoDao()

    suspend fun insertPhoto(photo: PhotoEntity) {
        photoDao.insertPhoto(photo)
    }

    suspend fun getActivityPhotos(activity: String): List<PhotoEntity> {
        return photoDao.getActivityPhotos(activity)
    }

    suspend fun updatePhoto(photo: PhotoEntity) {
        photoDao.updatePhoto(photo.id, photo.image!!)
    }

    suspend fun deleteAllPhotos() {
        photoDao.deleteAllPhotos()
    }

    suspend fun deletePhoto(photo: PhotoEntity) {
        photoDao.deletePhoto(photo.image!!, photo.activity!!)
    }

}