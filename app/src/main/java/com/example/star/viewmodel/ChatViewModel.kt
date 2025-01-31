package com.example.star.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.star.Constants
import com.example.star.model.GeminiMessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy { mutableStateListOf<GeminiMessageModel>()}

    private val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.geminiApiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch {
            val chat = generativeModel.startChat(
                history = messageList.map {
                    content(it.role){ text(it.message) }
                }.toList()
            )

            messageList.add(GeminiMessageModel(question, "user"))
            messageList.add(GeminiMessageModel(". . .", "model"))

            val response = chat.sendMessage(question)
            messageList.removeLastOrNull()
            messageList.add(GeminiMessageModel(response.text.toString(), "model"))
        }
    }
}