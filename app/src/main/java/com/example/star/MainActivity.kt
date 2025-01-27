package com.example.star

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.star.ui.theme.STARTheme
import com.example.star.view.HomePage
import com.example.star.view.LoginPage
import com.example.star.view.RegistrationPage
import com.example.star.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            STARTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun MainScreen(modifier: Modifier) {
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class]
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Routes.Login,
            builder = {
                composable(Routes.Login) {
                    LoginPage(modifier = Modifier.fillMaxSize(), authViewModel, navController)
                }
                composable(Routes.Register) {
                    RegistrationPage(modifier = Modifier.fillMaxSize(), authViewModel, navController)
                }
                composable(Routes.Home) {
                    HomePage(modifier = Modifier.fillMaxSize(), authViewModel, navController)
                }
            }
        )
    }

}