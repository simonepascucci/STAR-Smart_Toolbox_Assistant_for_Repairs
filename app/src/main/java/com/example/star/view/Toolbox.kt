package com.example.star.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.star.view.tools.LevelPage

data class ToolItem(val name: String, val icon: ImageVector)

@Composable
fun ToolboxPage() {
    val tools = listOf(
        ToolItem("Level", Icons.Filled.Home),
        ToolItem("Ruler", Icons.Filled.Create),
        ToolItem("Angle", Icons.Filled.Build),
        ToolItem("Compass", Icons.Filled.Settings),
        ToolItem("Plumb", Icons.Filled.Check),
        ToolItem("Info", Icons.Filled.Info)
    )
    var selectedTool by remember { mutableStateOf<ToolItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedTool == null) {
            Text(text = "Toolbox", modifier = Modifier.padding(bottom = 16.dp))
            ToolboxGrid(tools = tools) { tool ->
                selectedTool = tool
            }
        } else {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedTool = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
                Text(text = "Change tool", modifier = Modifier.weight(1f))
            }
            when (selectedTool!!.name) {
                "Level" -> LevelPage()
                /*"Ruler" -> RulerPage()
                "Angle" -> AnglePage()
                "Compass" -> CompassPage()
                "Plumb" -> PlumbPage()
                "Info" -> InfoPage()*/
            }
        }
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
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                modifier = Modifier.size(64.dp)
            )
        }
        Text(
            text = tool.name,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToolboxPagePreview() {
    ToolboxPage()
}