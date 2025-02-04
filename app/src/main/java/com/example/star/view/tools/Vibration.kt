package com.example.star.view.tools

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.star.view.ToolNameTitle
import kotlin.math.abs
import kotlin.math.sqrt

@SuppressLint("DefaultLocale", "MutableCollectionMutableState")
@Composable
fun VibrationMeterPage() {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var vibrationStatus by remember { mutableStateOf("Stable") }
    var lastAcceleration by remember { mutableStateOf(Triple(0f, 0f, 0f)) }
    var accelerationChange by remember { mutableDoubleStateOf(0.0) }
    var isMeasuring by remember { mutableStateOf(false) }
    var measurementResult by remember { mutableDoubleStateOf(0.0) }
    var measurementReadings by remember { mutableStateOf(mutableListOf<Double>()) }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate the change in acceleration
            val deltaX = abs(x - lastAcceleration.first)
            val deltaY = abs(y - lastAcceleration.second)
            val deltaZ = abs(z - lastAcceleration.third)

            accelerationChange = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble())

            // Update last acceleration
            lastAcceleration = Triple(x, y, z)

            // Determine vibration status based on the change in acceleration
            vibrationStatus = if (accelerationChange > 0.5) { // Adjust this threshold as needed
                "Vibrating"
            } else {
                "Stable"
            }

            // Add reading to list if measuring
            if (isMeasuring) {
                measurementReadings.add(accelerationChange)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    // Register and unregister the sensor listener based on the lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sensorManager.registerListener(
                    sensorEventListener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    // Calculate the average when measurement stops
    LaunchedEffect(isMeasuring) {
        if (!isMeasuring && measurementReadings.isNotEmpty()) {
            measurementResult = measurementReadings.average()
            measurementReadings.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        ToolNameTitle("Vibration Meter")

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF363737)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Vibration Status:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Text(
                    text = vibrationStatus,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = if (vibrationStatus == "Stable") Color(0xFF1A8032) else Color.Red
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Acceleration Change: ${String.format("%.2f m/s²", accelerationChange)}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isMeasuring = !isMeasuring },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMeasuring) Color.LightGray else Color(0xFFD25D1C)
                    )
                ) {
                    Text(text = if (isMeasuring) "Stop Measurement" else "Start Measurement")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (measurementResult > 0.0) {
                    Text(
                        text = "Average Vibration: ${String.format("%.2f m/s²", measurementResult)}",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}