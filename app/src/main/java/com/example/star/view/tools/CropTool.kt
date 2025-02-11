package com.example.star.view.tools

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun CropTool(bitmap: Bitmap, onCropConfirmed: (Int, Int, Int, Int) -> Unit) {
    var startX by remember { mutableFloatStateOf(100f) }
    var startY by remember { mutableFloatStateOf(100f) }
    var endX by remember { mutableFloatStateOf(400f) }
    var endY by remember { mutableFloatStateOf(400f) }

    var draggingCorner by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Display Image
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Image to Crop",
            modifier = Modifier.fillMaxWidth()
        )

        // Canvas for selection rectangle and corner handles
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        // Pinch to zoom
                        val centerX = (startX + endX) / 2
                        val centerY = (startY + endY) / 2

                        val newWidth = (endX - startX) * zoom
                        val newHeight = (endY - startY) * zoom

                        startX = max(0f, centerX - newWidth / 2)
                        startY = max(0f, centerY - newHeight / 2)
                        endX = min(bitmap.width.toFloat(), centerX + newWidth / 2)
                        endY = min(bitmap.height.toFloat(), centerY + newHeight / 2)
                    }
                }
        ) {
            // Draw cropping rectangle
            drawRect(
                color = Color.Red.copy(alpha = 0.5f),
                topLeft = Offset(startX, startY),
                size = Size(endX - startX, endY - startY)
            )

            // Draw draggable corner handles
            val handleSize = 24f
            val corners = listOf(
                Offset(startX, startY),  // Top-left
                Offset(endX, startY),    // Top-right
                Offset(startX, endY),    // Bottom-left
                Offset(endX, endY)       // Bottom-right
            )

            corners.forEach { corner ->
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(corner.x - handleSize / 2, corner.y - handleSize / 2),
                    size = Size(handleSize, handleSize)
                )
            }
        }

        // Dragging the corners
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Check if touch is near a corner
                        draggingCorner = listOf(
                            Pair(startX, startY), // Top-left
                            Pair(endX, startY),   // Top-right
                            Pair(startX, endY),   // Bottom-left
                            Pair(endX, endY)      // Bottom-right
                        ).minByOrNull { corner ->
                            val dx = offset.x - corner.first
                            val dy = offset.y - corner.second
                            dx * dx + dy * dy // Squared distance (no need for sqrt)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume() // Avoid touch conflicts

                        draggingCorner?.let { (cornerX, cornerY) ->
                            when {
                                cornerX == startX && cornerY == startY -> { // Top-left
                                    startX = max(0f, startX + dragAmount.x)
                                    startY = max(0f, startY + dragAmount.y)
                                }
                                cornerX == endX && cornerY == startY -> { // Top-right
                                    endX = min(bitmap.width.toFloat(), endX + dragAmount.x)
                                    startY = max(0f, startY + dragAmount.y)
                                }
                                cornerX == startX && cornerY == endY -> { // Bottom-left
                                    startX = max(0f, startX + dragAmount.x)
                                    endY = min(bitmap.height.toFloat(), endY + dragAmount.y)
                                }
                                cornerX == endX && cornerY == endY -> { // Bottom-right
                                    endX = min(bitmap.width.toFloat(), endX + dragAmount.x)
                                    endY = min(bitmap.height.toFloat(), endY + dragAmount.y)
                                }
                            }
                        }
                    },
                    onDragEnd = { draggingCorner = null }
                )
            })

        // Button to Confirm Crop
        Button(
            onClick = {
                onCropConfirmed(
                    startX.toInt(), startY.toInt(),
                    (endX - startX).toInt(), (endY - startY).toInt()
                )
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text("Crop", color = Color.White)
        }
    }
}
