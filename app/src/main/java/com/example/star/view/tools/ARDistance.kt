package com.example.star.view.tools

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
@Composable
fun ARDistance() {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val planeRenderer = remember { mutableStateOf(true) }
    val trackingFailureReason = remember { mutableStateOf<TrackingFailureReason?>(null) }
    val frame = remember { mutableStateOf<Frame?>(null) }
    var distance by remember { mutableStateOf<Float?>(null) }
    var currentAnchor by remember { mutableStateOf<Anchor?>(null) }
    var tapPosition by remember { mutableStateOf<Offset?>(null) }
    var showNoPlaneMessage by remember { mutableStateOf(false) }

    fun calculateDistance(anchor: Anchor?, frame: Frame?): Float? {
        if (anchor == null || frame == null) return null

        val cameraPose = frame.camera.pose
        val anchorPose = anchor.pose

        val dx = cameraPose.tx() - anchorPose.tx()
        val dy = cameraPose.ty() - anchorPose.ty()
        val dz = cameraPose.tz() - anchorPose.tz()

        return sqrt(dx * dx + dy * dy + dz * dz)
    }
    // Hide the "No plane" message after a delay
    LaunchedEffect(showNoPlaneMessage) {
        if (showNoPlaneMessage) {
            delay(3000) // 3 seconds
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
            onTrackingFailureChanged = {
                trackingFailureReason.value = it
            },
            onSessionUpdated = { session, updatedFrame ->
                frame.value = updatedFrame
                if (updatedFrame.camera.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                    session.configure(
                        session.config.apply {
                            planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                        }
                    )
                    val planes = session.getAllTrackables(Plane::class.java)
                    if (planes.isNotEmpty()) {
                        Log.d("ARScreen", "Planes detected: ${planes.size}")
                    } else {
                        Log.d("ARScreen", "No planes detected")
                    }
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
                            currentAnchor?.detach()
                            currentAnchor = hitOnPlane.createAnchorOrNull()
                            distance = calculateDistance(currentAnchor, frame.value)
                            tapPosition = Offset(e.x, e.y)
                            showNoPlaneMessage = false
                        } else {
                            showNoPlaneMessage = true
                        }
                    }
                }
            )
        )
        // Display the distance, the dot, and the "No plane" message
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (distance != null && tapPosition != null) {
                drawIntoCanvas { canvas ->
                    val distanceText = String.format("%.2f meters", distance)
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 50f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(
                        distanceText,
                        tapPosition!!.x,
                        tapPosition!!.y - 60, // Adjust position above the dot
                        textPaint
                    )
                    drawCircle(
                        color = Color.Red,
                        radius = 10.dp.toPx(),
                        center = tapPosition!!
                    )
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
                    canvas.nativeCanvas.drawText(
                        noPlaneText,
                        size.width / 2,
                        size.height / 2,
                        textPaint
                    )
                }
            }
        }
    }
}