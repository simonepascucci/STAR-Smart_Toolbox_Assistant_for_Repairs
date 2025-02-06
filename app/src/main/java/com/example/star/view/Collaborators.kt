package com.example.star.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.AddCollaboratorResult
import com.example.star.viewmodel.AuthViewModel

@Composable
fun Collaborators(activityViewModel: ActivityViewModel) {

    var showDialog by remember { mutableStateOf(false) }
    var collaboratorEmail by remember { mutableStateOf("") }
    val selectedActivity = activityViewModel.selectedActivity.observeAsState()
    val context = LocalContext.current
    val addCollaboratorResult by activityViewModel.addCollaboratorResult.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = addCollaboratorResult) {
        when (addCollaboratorResult) {
            is AddCollaboratorResult.Success -> {
                Toast.makeText(context, "Collaborator added successfully", Toast.LENGTH_SHORT).show()
                activityViewModel.addCollaboratorResult.value = AddCollaboratorResult.Idle
            }

            is AddCollaboratorResult.Failure -> {
                Toast.makeText(context, "Failed to add collaborator", Toast.LENGTH_SHORT).show()
                activityViewModel.addCollaboratorResult.value = AddCollaboratorResult.Idle
            }

            else -> {}
        }
    }

    if (showDialog) {
        AlertDialog(
            containerColor = Color(0xFFC5C5C5),
            onDismissRequest = {
                showDialog = false
                collaboratorEmail = ""
            },
            title = { Text("Insert collaborator email:", fontSize = 16.sp) },
            text = {
                Column {
                    OutlinedTextField(
                        value = collaboratorEmail,
                        onValueChange = { collaboratorEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        activityViewModel.addCollaborator(
                            selectedActivity.value!!.name,
                            collaboratorEmail
                        )
                        collaboratorEmail = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C))
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        collaboratorEmail = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        val authorText =
            if (selectedActivity.value!!.author == AuthViewModel().getEmail()) "You" else selectedActivity.value!!.author
        Text(
            text = "Author: ",
            color = Color(0xFF16590B),
            fontWeight = FontWeight.SemiBold
        )
        Text(text = authorText)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = "Collaborators:",
            color = Color(0xFF16590B),
            fontWeight = FontWeight.SemiBold
        )
        val collaborators = selectedActivity.value!!.collaborators
        if (collaborators.isEmpty()) {
            Text(text = "No collaborators yet")
        } else {
            collaborators.forEach { collaborator ->
                Text(text = collaborator)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val author = selectedActivity.value!!.author
        val currentUser = AuthViewModel().getEmail()
        if (author != currentUser) {
            Text(text = "Ask the owner to add a collaborator!", color = Color.Gray, fontSize = 14.sp)
        }else{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Add a new collaborator")
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFFD25D1C),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(32.dp)
                ) {
                    Icon(Icons.Filled.Add, "Add", tint = Color.White)
                }
            }
        }

    }
}