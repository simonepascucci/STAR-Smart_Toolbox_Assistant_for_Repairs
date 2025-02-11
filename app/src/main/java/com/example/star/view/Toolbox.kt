package com.example.star.view

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.star.R
import com.example.star.view.tools.ARDistance
import com.example.star.view.tools.ARRuler
import com.example.star.view.tools.LevelPage
import com.example.star.view.tools.PhotosPage
import com.example.star.view.tools.VibrationMeterPage
import com.example.star.viewmodel.ActivityViewModel

sealed class ToolIcon {
    data class DrawableIcon(val id: Int) : ToolIcon()
    data class VectorIcon(val imageVector: ImageVector) : ToolIcon()
}

data class ToolItem(val name: String, val icon: ToolIcon)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ToolboxPage(activityViewModel: ActivityViewModel) {
    var torchEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember {
        try {
            cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            null
        }
    }

    DisposableEffect(torchEnabled) {
        if (cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, torchEnabled)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        onDispose {
            if (cameraId != null) {
                try {
                    cameraManager.setTorchMode(cameraId, false)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }

    val tools = listOf(
        ToolItem("Level", ToolIcon.DrawableIcon(R.drawable.levelicon)),
        ToolItem("Measuring Tape", ToolIcon.DrawableIcon(R.drawable.metericon)),
        ToolItem("Vibration Meter", ToolIcon.DrawableIcon(R.drawable.vibrationicon)),
        ToolItem("Distance", ToolIcon.DrawableIcon(R.drawable.distanceicon)),
        ToolItem("Photos", ToolIcon.VectorIcon(Icons.Filled.PhotoLibrary)),
        ToolItem(
            "Flashlight",
            ToolIcon.VectorIcon(if (torchEnabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff)
        )
    )

    var selectedTool by remember { mutableStateOf<ToolItem?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedTool == null) {
            Card(
                modifier = Modifier
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Toolbox",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            ToolboxGrid(tools = tools) { tool ->
                if (tool.name != "Flashlight") selectedTool = tool
                else {
                    torchEnabled = !torchEnabled
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedTool = null }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(text = "Change tool", modifier = Modifier.weight(1f))
                    if (selectedTool?.name != "Flashlight") {
                        TorchToggleButton(
                            torchEnabled = torchEnabled,
                            onToggle = { torchEnabled = !torchEnabled },
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
                when (selectedTool!!.name) {
                    "Level" -> LevelPage()
                    "Vibration Meter" -> VibrationMeterPage()
                    "Measuring Tape" -> ARRuler()
                    "Distance" -> ARDistance()
                    "Photos" -> PhotosPage(activityViewModel)
                }
            }
        }
    }
}

@Composable
fun TorchToggleButton(torchEnabled: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
            .background(color = Color.Gray, shape = CircleShape)
            .size(48.dp)
    ) {
        Icon(
            imageVector = if (torchEnabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
            contentDescription = "Toggle Flashlight",
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
    }
}

@Composable
fun ToolboxGrid(tools: List<ToolItem>, onToolClick: (ToolItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(tools) { tool ->
            ToolButton(tool = tool, onToolClick = onToolClick)
        }
    }
}

@Composable
fun ToolButton(tool: ToolItem, onToolClick: (ToolItem) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { onToolClick(tool) }, // Call the lambda here
            modifier = Modifier.size(100.dp)
        ) {
            when (val icon = tool.icon) {
                is ToolIcon.DrawableIcon -> {
                    Icon(
                        painter = painterResource(id = icon.id),
                        contentDescription = tool.name,
                        modifier = Modifier.size(64.dp)
                    )
                }

                is ToolIcon.VectorIcon -> {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = tool.name,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        Text(
            text = tool.name,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToolNameTitle(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
