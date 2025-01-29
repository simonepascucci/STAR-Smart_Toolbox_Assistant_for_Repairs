package com.example.star.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.star.model.Activity
import com.example.star.viewmodel.ActivityViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewActivity(email: String, activityViewModel: ActivityViewModel) {
    val context = LocalContext.current

    val categories = listOf("Electrical", "Mechanical", "Plumbing", "Other")
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
                    collaborators = mutableListOf(email),
                    status = "STARTED",
                    createdAt = Timestamp.now().toString()
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


@Composable
fun DisplayActivities(email: String, activityViewModel: ActivityViewModel) {
    // Trigger the fetch of activities when the composable is first composed
    activityViewModel.getUserActivities(email)

    // Observe the LiveData and get the list of activities
    val userActivities by activityViewModel.activityData.observeAsState(initial = emptyList())

    // Use LazyColumn for scrollable list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
    ) {
        items(userActivities) { activity ->
            ActivityCard(activity = activity)
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    // State to control the visibility of extra info
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
                    // Title (Activity Name)
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Category
                    Text(
                        text = "Category: ${activity.category}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Status Indicator
                StatusIndicator(status = activity.status)
            }

            // Animated visibility for extra info
            AnimatedVisibility(visible = showMoreInfo) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Author: ${activity.author}")
                    Text(text = "Collaborators: ${activity.collaborators.joinToString(", ")}")
                    Text(text = "Status: ${activity.status}")
                    Text(text = "Created At: ${activity.createdAt}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between buttons
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show More Info Button
                Button(
                    onClick = { showMoreInfo = !showMoreInfo },
                    modifier = Modifier.weight(1f) // Take up available space
                ) {
                    Text(text = if (showMoreInfo) "Show Less" else "Show Details")
                }

                // Enter Activity Button
                Button(
                    onClick = { /* Handle entering the activity here */ },
                    modifier = Modifier.weight(1f) // Take up available space
                ) {
                    Text(text = "Enter Activity")
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val color = when (status.lowercase()) {
        "started" -> Color.Green
        "paused" -> Color.Red
        "completed" -> Color.Yellow
        else -> Color.Gray // Default color if status is unknown
    }

    val label = when (status.lowercase()) {
        "started" -> "Started"
        "paused" -> "Paused"
        "completed" -> "Completed"
        else -> "Unknown"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(24.dp)) {
            drawCircle(color = color, center = Offset(size.width / 2, size.height / 2), radius = size.minDimension / 2)
        }
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}