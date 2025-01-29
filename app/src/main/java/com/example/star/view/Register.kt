package com.example.star.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.star.R
import com.example.star.Routes
import com.example.star.model.UserRepository
import com.example.star.viewmodel.AuthState
import com.example.star.viewmodel.AuthViewModel
import com.example.star.viewmodel.UserViewModel

@Composable
fun RegistrationPage(authViewModel: AuthViewModel, userViewModel: UserViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate(Routes.Home)
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.starlogo),
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF16590B),
                focusedLabelColor = Color(0xFF16590B)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF16590B),
                focusedLabelColor = Color(0xFF16590B)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = validatePassword(it)
            },
            label = { Text("Password") },
            isError = passwordError.isNotEmpty(),
            supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) else null },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                RadioButton(
                    selected = confirmPasswordVisible,
                    onClick = {
                        passwordVisible = !passwordVisible
                        confirmPasswordVisible = !confirmPasswordVisible
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF16590B),
                focusedLabelColor = Color(0xFF16590B)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = if (it != password) "Passwords do not match" else ""
            },
            label = { Text("Confirm Password") },
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = { if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError) else null },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {

            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF16590B),
                focusedLabelColor = Color(0xFF16590B)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            userViewModel.addNewUser(email, username)
            authViewModel.signup(email, password)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF16590B),
                contentColor = Color.White
            ),
            enabled = email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword == password
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row (modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center) {
            Text(text = "Already have an account? ", color = Color.Gray)
            Text(text = " Login", color = Color(0xFFD25D1C),modifier = Modifier.clickable {
                navController.navigate(Routes.Login)
            })
        }
    }
}

private fun validatePassword(password: String): String {
    if (password.length < 8) {
        return "Password must be at least 8 characters long"
    }
    if (!password.any { it.isUpperCase() }) {
        return "Password must contain at least one uppercase letter"
    }
    if (!password.any { it.isDigit() }) {
        return "Password must contain at least one number"
    }
    return ""
}