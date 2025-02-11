package com.example.star.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE activity = :activity")
    suspend fun getActivityPhotos(activity: String): List<PhotoEntity>

    @Query("UPDATE photos SET image = :image WHERE id = :id")
    suspend fun updatePhoto(id: Int, image: ByteArray)

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()

    @Query("DELETE FROM photos WHERE image = :image AND activity = :activity")
    suspend fun deletePhoto(image: ByteArray, activity: String)
}