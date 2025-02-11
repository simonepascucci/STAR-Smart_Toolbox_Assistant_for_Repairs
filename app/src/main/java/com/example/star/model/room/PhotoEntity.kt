package com.example.star.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val image: ByteArray ?= null,
    val activity: String ?= null,
)
