package com.example.star.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.star.Routes
import com.example.star.model.Activity
import com.example.star.viewmodel.ActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CreateNewActivity(email: String, activityViewModel: ActivityViewModel) {
    val context = LocalContext.current

    val categories = listOf("Electrical", "Gardening", "Home decor", "Mechanical","Painting", "Plumbing", "Smart home setup", "Wood working","Other")
    var activityName by remember { mutableStateOf("") }
    var activityCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text("Activity Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF16590B),
                focusedLabelColor = Color(0xFF16590B)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = activityCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Activity Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF16590B),
                    focusedLabelColor = Color(0xFF16590B)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            activityCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                activityViewModel.addNewActivity(
                    name = activityName,
                    category = activityCategory,
                    author = email,
                    collaborators = mutableListOf(),
                    status = "STARTED"
                )

                Toast.makeText(context,"Activity created", Toast.LENGTH_SHORT).show()
                activityName = ""
                activityCategory = ""
                activityViewModel.disableForm()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16590B))
        ) {
            Text("Create activity", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayActivities(
    email: String,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val userActivities by activityViewModel.activityData.observeAsState(initial = emptyList())
    val userCollaborations by activityViewModel.collaborationsData.observeAsState(initial = emptyList())

    fun refreshActivities() {
        isRefreshing = true
        activityViewModel.getUserActivities(email)
        activityViewModel.getUserCollaborations(email)
        isRefreshing = false
    }

    LaunchedEffect(key1 = true) {
        refreshActivities()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { refreshActivities() }
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            val loadedActivitiesFlag = activityViewModel.activityData.observeAsState().value == null
            if (loadedActivitiesFlag) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "You currently have ${userActivities.size} activities:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    if (userActivities.isEmpty()) {
                        item {
                            Text(text = "No activities found.", modifier = Modifier.padding(8.dp))
                            Row(modifier = Modifier.padding(8.dp)) {
                                Text(text = "Click on ")
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "info",
                                    tint = Color(0xFFD25D1C)
                                )
                                Text(text = " to add a new activity.")
                            }
                        }
                    } else {
                        item {
                            Carousel(
                                items = userActivities,
                                navController = navController,
                                activityViewModel = activityViewModel
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You are collaborating in the following activities:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    if (userCollaborations.isEmpty()) {
                        item {
                            Text(
                                text = "You are not collaborating in any activity.",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        item {
                            Carousel(
                                items = userCollaborations,
                                navController = navController,
                                activityViewModel = activityViewModel
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
    navController: NavController,
    activityViewModel: ActivityViewModel
) {
    var showMoreInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Category: ${activity.category}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                StatusIndicator(status = activity.status)
            }

            AnimatedVisibility(visible = showMoreInfo) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Author: ${activity.author}")
                    if (activity.collaborators.isNotEmpty()) Text(text = "Collaborators: ${activity.collaborators.joinToString(", ")}")
                    Text(text = "Created: ${activity.createdAt.toDate()}")
                    if (activity.status == "COMPLETED") Text(text = "Completed: ${activity.completedAt!!.toDate()}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showMoreInfo = !showMoreInfo },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "info", tint = Color(0xFFD25D1C))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (showMoreInfo) "Show Less" else "Show Details", color = Color(0xFFD25D1C))
                    }
                }

                Button(
                    onClick = {
                        activityViewModel.selectActivity(activity)
                        navController.navigate(route = Routes.Activity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16590B)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Enter Activity", textAlign = TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun Carousel(
    items: List<Activity>,
    navController: NavController,
    activityViewModel: ActivityViewModel
) {
    val scrollState = rememberScrollState()
    val cardWidth = 300.dp
    val cardPadding = 8.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { activity ->
            Box(
                modifier = Modifier
                    .width(cardWidth)
                    .padding(horizontal = cardPadding)
            ) {
                ActivityCard(
                    activity = activity,
                    navController = navController,
                    activityViewModel = activityViewModel
                )
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val (color, icon) = when (status.lowercase()) {
        "started" -> Pair(Color(0xFF16590B), null)
        "paused" -> Pair(Color(0xFF393939), Icons.Filled.Lock)
        "completed" -> Pair(Color(0xFFD25D1C), Icons.Filled.CheckCircle)
        else -> Pair(Color.Gray, null)
    }

    val label = when (status.lowercase()) {
        "started" -> "Started"
        "paused" -> "Paused"
        "completed" -> "Completed"
        else -> "Unknown"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (icon != null) {
            StatusIcon(icon = icon, color = color)
        } else {
            StatusCircle(color = color)
        }
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun StatusCircle(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        drawCircle(
            color = color,
            center = Offset(size.width / 2, size.height / 2),
            radius = size.minDimension / 2
        )
    }
}

@Composable
fun StatusIcon(icon: ImageVector, color: Color) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}
