package com.example.star.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.star.R
import com.example.star.model.GeminiMessageModel
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.ChatViewModel

@Composable
fun GeminiChatPage(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    chatViewModel: ChatViewModel
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        MessageList(modifier = Modifier.weight(1f), messageList = chatViewModel.messageList)
        MessageInput(
            onMessageSend = {
                chatViewModel.sendMessage(it)
            },
            activityViewModel,
            hasMessages = chatViewModel.messageList.isNotEmpty()
        )
    }
}

@Composable
fun MessageInput(
    onMessageSend: (String) -> Unit,
    activityViewModel: ActivityViewModel,
    hasMessages: Boolean
) {
    var message by remember {
        mutableStateOf(
            if (!hasMessages) {
                "I'm working in this field: ${activityViewModel.selectedActivity.value?.category}, \nsince you are very smart, could you help me with this activity: ${activityViewModel.selectedActivity.value?.name} ?"
            } else {
                ""
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
        }
    }
}

@Composable
fun MessageList(modifier: Modifier, messageList: List<GeminiMessageModel>) {

    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.padding(bottom = 32.dp),
                painter = painterResource(id = R.drawable.geminilogo),
                contentDescription = "Gemini Logo"
            )
            Icon(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = "Ask Gemini",
                tint = Color(0xFF627DD9)
            )
            Text(text = "Let me help you with your project!")
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(bottom = 8.dp),
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                DisplayMessages(messageModel = it)
            }
        }
    }
}

@Composable
fun DisplayMessages(messageModel: GeminiMessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 0.dp else 70.dp,
                        end = if (isModel) 70.dp else 0.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) Color(0xFF16590B) else Color(0xFFD25D1C))
                    .padding(16.dp)
            ) {
                Text(text = messageModel.message, fontWeight = FontWeight.W700, color = Color.White)
            }
        }
    }
}