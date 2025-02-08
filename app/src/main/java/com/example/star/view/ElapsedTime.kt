package com.example.star.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.star.model.api.NetworkResponse
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.ElapsedTimeViewModel
import com.google.firebase.Timestamp

@Composable
fun ElapsedTime(
    activityViewModel: ActivityViewModel,
    elapsedTimeViewModel: ElapsedTimeViewModel
) {
    val selectedActivity = activityViewModel.selectedActivity.observeAsState()
    val elapsedTime = elapsedTimeViewModel.elapsedTimeResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7E8B98),
                contentColor = Color.White
            ),
            onClick = {
                elapsedTimeViewModel.fetchData(
                    timestamp1 = selectedActivity.value!!.createdAt.seconds,
                    timestamp2 = Timestamp.now().seconds
                )
            }
        ) {
            Text(text = "Check elapsed time")
        }
        when (val result = elapsedTime.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }

            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }

            is NetworkResponse.Success -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Elapsed Time:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ElapsedTimeCards(
                    days = result.data.days,
                    hours = result.data.hours,
                    minutes = result.data.minutes
                )
            }

            null -> {}
        }
    }
}

@Composable
fun ElapsedTimeCards(days: String, hours: String, minutes: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ElapsedTimeCard(value = days, label = "Days")
        ElapsedTimeCard(value = hours, label = "Hours")
        ElapsedTimeCard(value = minutes, label = "Minutes")
    }
}

@Composable
fun ElapsedTimeCard(value: String, label: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}