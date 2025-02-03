package com.example.star.view.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.star.view.ToolNameTitle
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun LevelPage() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometerSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    var pitch by remember { mutableFloatStateOf(0f) }
    var roll by remember { mutableFloatStateOf(0f) }

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // Extract accelerometer data
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate pitch and roll (corrected formulas)
            val newPitch = atan2(y.toDouble(), sqrt(x * x + z * z).toDouble()).toDegrees().toFloat()
            val newRoll = atan2(-x.toDouble(), sqrt(y * y + z * z).toDouble()).toDegrees().toFloat()

            // Update the state variables
            pitch = newPitch
            roll = newRoll
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sensorManager.registerListener(
                    sensorEventListener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    LevelUI(pitch = pitch, roll = roll)
}

@Composable
fun LevelUI(pitch: Float, roll: Float) {
    val animatedPitch by animateFloatAsState(targetValue = pitch, label = "pitchAnimation")
    val animatedRoll by animateFloatAsState(targetValue = roll, label = "rollAnimation")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ToolNameTitle(title = "Spirit Level")

        Box(
            modifier = Modifier
                .size(250.dp)
                .background(color = Color(0xFF363737)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.minDimension / 2 - 20.dp.toPx()

                // Draw the outer circle
                drawCircle(
                    color = Color(0xFF1A8032),
                    radius = radius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 5.dp.toPx())
                )

                // Draw the center circle
                drawCircle(
                    color = Color(0xFF1A8032),
                    radius = 10.dp.toPx(),
                    center = Offset(centerX, centerY)
                )

                // Draw the bubble (corrected bubble position)
                val bubbleX = centerX + animatedRoll * (radius / 30)
                val bubbleY = centerY - animatedPitch * (radius / 30) // Invert pitch for correct direction
                drawCircle(
                    color = Color(0xFFD25D1C),
                    radius = 20.dp.toPx(),
                    center = Offset(bubbleX, bubbleY)
                )
            }
        }

        Text(
            text = "Pitch: ${animatedPitch.format(2)}°",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = "Roll: ${animatedRoll.format(2)}°",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Always show one of the two texts
        if (abs(animatedPitch) < 2 && abs(animatedRoll) < 2) {
            Text(
                text = "Level!",
                color = Color(0xFF1A8032),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )
        } else {
            Text(
                text = "Not Level",
                color = Color.Red,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun Double.toDegrees(): Double = Math.toDegrees(this)