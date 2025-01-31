package com.example.star.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.star.model.GeminiMessageModel
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.ChatViewModel

@Composable
fun ActivityPage(
    activityViewModel: ActivityViewModel,
    navController: NavHostController,
    chatViewModel: ChatViewModel
) {

    var selectedItem by remember { mutableIntStateOf(1) } // 0: Toolbox, 1: Activity Home, 2: Ask Gemini
    val selectedActivity = activityViewModel.selectedActivity.observeAsState()

    Scaffold(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            Banner()
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Toolbox") },
                    label = { Text("Toolbox") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Activity Home") },
                    label = { Text("Activity Home") },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Ask Gemini") },
                    label = { Text("Ask Gemini") },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column (
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.TopCenter)
            ) {
                if (selectedItem == 1) {
                    ActivityHomePage(activityViewModel)
                }
                if (selectedItem == 2) {
                    GeminiChatPage(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),activityViewModel, chatViewModel)
                }
            }
        }
    }
}

@Composable
fun ActivityHomePage(activityViewModel: ActivityViewModel) {

    val selectedActivity = activityViewModel.selectedActivity.observeAsState()

    if (selectedActivity.value == null) {
        Text("No activity selected")
        return
    }

        Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedActivity.value!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

            }
        }
            StatusButtons(activityViewModel)

            if (selectedActivity.value!!.status == "COMPLETED") {
                Text(
                    "Activity completed!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(text = "Here are your activity details:")
                Text(text = "Category: ${selectedActivity.value!!.category}")
                Text(text = "Author: ${selectedActivity.value!!.author}")
                Text(text = "Created: ${selectedActivity.value!!.createdAt.toDate()}")
                Text(text = "Completed: ${selectedActivity.value!!.completedAt!!.toDate()}")
            }
    }
}

@Composable
fun StatusButtons(activityViewModel: ActivityViewModel) {

    val selectedActivity = activityViewModel.selectedActivity.observeAsState()

    when (selectedActivity.value!!.status.lowercase()) {
        "started" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF393939)),
                    onClick = {
                        activityViewModel.updateActivity(
                            selectedActivity.value!!.name,
                            "status",
                            "PAUSED"
                        )
                    }
                ) {
                    Text(text = "Pause")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C)),
                    onClick = {
                        activityViewModel.setCompleted(selectedActivity.value!!.name)
                    }
                ) {
                    Text(text = "Complete")
                }
            }
        }

        "paused" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16590B)),
                    onClick = {
                        activityViewModel.updateActivity(
                            selectedActivity.value!!.name,
                            "status",
                            "STARTED"
                        )
                    }
                ) {
                    Text(text = "Restart")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C)),
                    onClick = {
                        activityViewModel.setCompleted(selectedActivity.value!!.name)
                    }
                ) {
                    Text(text = "Complete")
                }
            }
        }

        "completed" -> {
            Button( //toTestOnly
                onClick = {
                    activityViewModel.updateActivity(
                        selectedActivity.value!!.name,
                        "status",
                        "STARTED"
                    )
                }
            ) {
                Text(text = "Restart")
            }
        }
    }
}

@Composable
fun GeminiChatPage(modifier: Modifier, activityViewModel: ActivityViewModel, chatViewModel: ChatViewModel){
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
        MessageList(modifier = Modifier.weight(1f), messageList = chatViewModel.messageList)
        MessageInput(onMessageSend = {
            chatViewModel.sendMessage(it)
        }, activityViewModel)
    }

}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit, activityViewModel: ActivityViewModel) {

    var message by remember { mutableStateOf(
        "I'm working in this field: ${activityViewModel.selectedActivity.value!!.category}, \nsince you are very smart, could you help me with this activity: ${activityViewModel.selectedActivity.value!!.name} ?"
    ) }

    Row (
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
        Column (
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Healing, contentDescription = "Gemini")
            Text(text = "Let me help you with your repair!")
        }
    }
    else{
        LazyColumn (
            modifier = modifier.padding(bottom = 8.dp),
            reverseLayout = true
        ){
            items(messageList.reversed()){
                DisplayMessages(messageModel = it)
            }
        }
    }

}

@Composable
fun DisplayMessages(messageModel: GeminiMessageModel) {
    val isModel = messageModel.role == "model"

    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Box (
            modifier = Modifier.fillMaxWidth()
        ){
            Box(modifier = Modifier.align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
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
