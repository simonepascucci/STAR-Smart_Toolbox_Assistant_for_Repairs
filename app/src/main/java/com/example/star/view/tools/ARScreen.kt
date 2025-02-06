package com.example.star.view.tools

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ARRuler() {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val planeRenderer = remember { mutableStateOf(true) }
    val trackingFailureReason = remember { mutableStateOf<TrackingFailureReason?>(null) }
    val frame = remember { mutableStateOf<Frame?>(null) }
    val anchors = remember { mutableStateListOf<Anchor>() }
    var screenPositions by remember { mutableStateOf(listOf<Offset>()) }
    var clearScreen by remember { mutableStateOf(false) }
    var tapPositions by remember { mutableStateOf(listOf<Offset>()) }
    var showNoPlaneMessage by remember { mutableStateOf(false) }

    fun clearScreen() {
        anchors.clear()
        screenPositions = emptyList()
        tapPositions = emptyList()
        clearScreen = true
        showNoPlaneMessage = false
    }

    LaunchedEffect(showNoPlaneMessage) {
        if (showNoPlaneMessage) {
            delay(3000)
            showNoPlaneMessage = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            collisionSystem = collisionSystem,
            planeRenderer = planeRenderer.value,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = { trackingFailureReason.value = it },
            onSessionUpdated = { session, updatedFrame ->
                frame.value = updatedFrame
                if (updatedFrame.camera.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                    session.configure(session.config.apply {
                        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    })
                }
            },
            sessionConfiguration = { session, config ->
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { e: MotionEvent, node: io.github.sceneview.node.Node? ->
                    if (node == null) {
                        val hitTestResult = frame.value?.hitTest(e.x, e.y)
                        val hitOnPlane = hitTestResult?.firstOrNull {
                            it.isValid(depthPoint = false, point = false) && it.trackable is Plane
                        }
                        if (hitOnPlane != null) {
                            val anchor = hitOnPlane.createAnchorOrNull()
                            if (anchor != null) {
                                if (anchors.isEmpty()) {
                                    anchors.clear()
                                    screenPositions = emptyList()
                                    tapPositions = emptyList()
                                }
                                anchors.add(anchor)
                                screenPositions = screenPositions + Offset(e.x, e.y)
                                tapPositions = tapPositions + Offset(e.x, e.y)
                                clearScreen = false
                                showNoPlaneMessage = false
                            }
                        } else {
                            showNoPlaneMessage = true
                        }
                    }
                }
            )
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!clearScreen) {
                for (i in 0 until screenPositions.size - 1) {
                    val start = screenPositions[i]
                    val end = screenPositions[i + 1]
                    drawLine(color = Color.White, start = start, end = end, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                    val distance = calculateDistance(anchors[i], anchors[i + 1])
                    val distanceText = String.format("%.2f cm", distance * 100)
                    val midPoint = Offset((start.x + end.x) / 2, (start.y + end.y) / 2)
                    drawIntoCanvas { canvas ->
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 40f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        canvas.nativeCanvas.drawText(distanceText, midPoint.x, midPoint.y, textPaint)
                    }
                }
                tapPositions.forEach { offset ->
                    drawCircle(color = Color(0xFFD25D1C), radius = 6.dp.toPx(), center = offset)
                }
            }
            if (showNoPlaneMessage) {
                drawIntoCanvas { canvas ->
                    val noPlaneText = "Find a plane before clicking"
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 60f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(noPlaneText, size.width / 2, size.height / 2, textPaint)
                }
            }
        }
        Column(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)) {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD25D1C)), onClick = { clearScreen() }) {
                Text("Clear")
            }
        }
    }
}

fun calculateDistance(anchor1: Anchor, anchor2: Anchor): Float {
    val position1 = anchor1.pose.translation
    val position2 = anchor2.pose.translation

    val dx = position1[0] - position2[0]
    val dy = position1[1] - position2[1]
    val dz = position1[2] - position2[2]

    return sqrt(dx * dx + dy * dy + dz * dz)
}