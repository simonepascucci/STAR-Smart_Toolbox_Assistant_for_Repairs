package com.example.star.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.star.R
import com.example.star.Routes
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.AuthViewModel
import com.example.star.viewmodel.ChatViewModel
import com.example.star.viewmodel.ElapsedTimeViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ActivityPage(
    activityViewModel: ActivityViewModel,
    navController: NavHostController,
    chatViewModel: ChatViewModel,
    elapsedTimeViewModel: ElapsedTimeViewModel
) {

    var selectedItem by remember { mutableIntStateOf(1) } // 0: Toolbox, 1: Activity Home, 2: Ask Gemini

    Scaffold(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            Banner()
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF363737),
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = "Toolbox",
                            modifier = Modifier.size(32.dp),
                            tint = if (selectedItem == 0) Color.White else Color(0xFFC5C5C5)
                        )
                    },
                    label = { Text("Toolbox", color = if (selectedItem == 0) Color.White else Color(0xFFC5C5C5)) },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF505050)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Activity Home",
                            modifier = Modifier.size(32.dp),
                            tint = if (selectedItem == 1) Color.White else Color(0xFFC5C5C5)
                        )
                    },
                    label = { Text("Activity Home", color = if (selectedItem == 1) Color.White else Color(0xFFC5C5C5)) },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF505050)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(R.drawable.geministar),
                            contentDescription = "Ask Gemini",
                            modifier = Modifier.size(32.dp),
                            tint = if (selectedItem == 2) Color.White else Color(0xFFC5C5C5)
                        )
                    },
                    label = { Text("Ask Gemini", color = if (selectedItem == 2) Color.White else Color(0xFFC5C5C5)) },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF505050)
                    )
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
                    ActivityHomePage(activityViewModel, navController, elapsedTimeViewModel)
                }
                if (selectedItem == 2) {
                    GeminiChatPage(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),activityViewModel, chatViewModel)
                }
                if (selectedItem == 0) {
                    ToolboxPage()
                }
            }
        }
    }
}

@Composable
fun ActivityHomePage(
    activityViewModel: ActivityViewModel,
    navController: NavHostController,
    elapsedTimeViewModel: ElapsedTimeViewModel
) {

    val selectedActivity = activityViewModel.selectedActivity.observeAsState()

    if (selectedActivity.value == null) {
        Text("No activity selected")
        return
    }

    val author = selectedActivity.value!!.author
    val currentUser = AuthViewModel().getEmail()

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
            if (author == currentUser) {
                StatusButtons(activityViewModel)
            }
            else{
                Text(
                    text = "Only the owner can change the activity status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }


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
                Text(text = "Collaborators: ${selectedActivity.value!!.collaborators}")
                Text(text = "Created: ${selectedActivity.value!!.createdAt.toDate()}")
                Text(text = "Completed: ${selectedActivity.value!!.completedAt!!.toDate()}")
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        elapsedTimeViewModel.resetElapsedTime()
                        ElapsedTime(activityViewModel, elapsedTimeViewModel)
                    }
                    item {
                        HomePageSensorsReading()
                    }
                    item {
                        Collaborators(activityViewModel)
                    }
                    item {
                        MoreOptions(activityViewModel, navController)
                    }
                }
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
fun MoreOptions(activityViewModel: ActivityViewModel, navController: NavHostController) {
    val selectedActivity = activityViewModel.selectedActivity.observeAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRemoveCollabDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val activityIsMine = selectedActivity.value!!.author == AuthViewModel().getEmail()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF363737),
                containerColor = Color.Transparent,
            )
        ) {
            Text(text = "More settings")
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "More settings",
                tint = Color(0xFF363737)
            )
        }
        if (expanded) {
            if (activityIsMine) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(start = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red,
                        containerColor = Color.Transparent,
                    ),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text(text = "Delete this activity")
                }
            } else {
                OutlinedButton(
                    onClick = { showRemoveCollabDialog = true },
                    modifier = Modifier.padding(start = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red,
                        containerColor = Color.Transparent,
                    ),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text(text = "Remove collaboration")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFFEEEEEE),
            title = { Text("Delete: ${selectedActivity.value!!.name}") },
            text = { Text("Are you sure you want to delete this activity? This action is irreversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        activityViewModel.deleteActivity(selectedActivity.value!!.name)
                        navController.navigate(Routes.Home)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Neutral color
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C)) // Orange color
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
    if (showRemoveCollabDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveCollabDialog = false },
            containerColor = Color(0xFFEEEEEE),
            text = { Text("Are you sure you want to remove your collaboration in: ${selectedActivity.value!!.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        activityViewModel.removeCollaboration(selectedActivity.value!!.name, AuthViewModel().getEmail())
                        navController.navigate(Routes.Home)
                        showRemoveCollabDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Neutral color
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showRemoveCollabDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C)) // Orange color
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

