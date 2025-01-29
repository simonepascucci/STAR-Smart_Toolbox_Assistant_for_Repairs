package com.example.star.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    val activityViewModel = ActivityViewModel()
    val showForm = activityViewModel.showForm.observeAsState()

    userViewModel.getUserData(email)
    val userData = userViewModel.userData.observeAsState()


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            userData.value?.email?.let {
                Text(text = "Your Mail: $it")
            }
            userData.value?.username?.let {
                Text(text = "Your Username: $it")
            }

            if (showDialog) {
                AlertDialog(
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

            if (showForm.value == true) {
                CreateNewActivity(email, activityViewModel)
            }
            else{
                DisplayActivities(email, activityViewModel)
            }

        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFD25D1C),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, "Add", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                authViewModel.signOut()
            }) {
                Text(text = "Logout")
            }
        }
    }
}

