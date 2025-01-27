package com.example.star.model

import kotlinx.coroutines.delay

class userRepository {

    suspend fun fetchUserData(): UserData{
        delay(2000)
        return UserData("John", age = 22)
    }

}