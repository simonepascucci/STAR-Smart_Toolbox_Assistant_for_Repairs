package com.example.star.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.star.Routes
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.AuthState
import com.example.star.viewmodel.AuthViewModel
import com.example.star.viewmodel.UserViewModel

@Composable
fun HomePage(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(Routes.Login)
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    val email = authViewModel.getEmail()

    val showForm = activityViewModel.showForm.observeAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)) {
        // Banner (Top)
        Banner()
        // Main Content (Center)
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 90.dp, bottom = 90.dp)
                .align(Alignment.Center), // Center the content vertically
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Welcome Header
            WelcomeHeader(userViewModel, email)

            // Dialog
            if (showDialog) {
                AlertDialog(
                    containerColor = Color(0xFFC5C5C5),
                    onDismissRequest = { showDialog = false },
                    title = { Text("Do you want to add a new activity?", fontSize = 16.sp) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                activityViewModel.enableForm()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C))
                        ) {
                            Text("Yes", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("No", color = Color.White)
                        }
                    }
                )
            }

            // Content based on showForm
            if (showForm.value == true) {
                CreateNewActivity(email, activityViewModel)
            } else {
                DisplayActivities(email, activityViewModel, navController)
            }
        }

        // Logout and Floating Action Button (Bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars) // Add padding for navigation bars
                .padding(16.dp), // Add some padding around the buttons
            horizontalArrangement = Arrangement.SpaceBetween, // Space between the buttons
            verticalAlignment = Alignment.Bottom
        ) {
            // Logout Button (Bottom Start)
            Button(
                onClick = { authViewModel.signOut() },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = "Logout")
            }

            // Floating Action Button (Bottom End)
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFD25D1C),
                shape = CircleShape,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add", tint = Color.White)
            }
        }
    }
}

@Composable
fun WelcomeHeader(userViewModel: UserViewModel, email: String) {
    userViewModel.getUserData(email)
    val userData = userViewModel.userData.observeAsState()
    if (userData.value != null && userData.value!!.username != "") {
        Text(
            text = "Welcome Back, ${userData.value!!.username}!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "Your email: ",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF16590B)
            )
            Text(
                text = userData.value!!.email,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}