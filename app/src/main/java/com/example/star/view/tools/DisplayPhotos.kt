package com.example.star.view.tools

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.star.R
import com.example.star.model.room.BitmapConverter
import com.example.star.model.room.PhotoEntity
import com.example.star.viewmodel.PhotoViewModel

// Singleton BitmapConverter
object BitmapConverterSingleton {
    val converter = BitmapConverter()
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun DisplayPhotos(
    modifier: Modifier = Modifier,
    photos: List<PhotoEntity>,
    photoViewModel: PhotoViewModel
) {
    var selectedPhoto by remember { mutableStateOf<PhotoEntity?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var rotated by remember { mutableStateOf(false) }
    var marked by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photo ->
            PhotoGridItem(photo) {
                selectedPhoto = photo
            }
        }
    }

    if (selectedPhoto != null) {
        val bitmap = BitmapConverterSingleton.converter.toBitmap(selectedPhoto!!.image!!)
        PhotoDialog(
            photo = selectedPhoto!!,
            onDismiss = { selectedPhoto = null },
            onDelete = {
                if (!rotated && !marked) {
                    showDeleteConfirmation = true
                } else {
                    Toast.makeText(context, "Save the changes first", Toast.LENGTH_SHORT).show()
                }
            },
            onRotate = {
                rotated = true
                val rotatedBitmap = OpenCVUtils.rotateBitmap90Clockwise(bitmap)
                val rotatedByteArray =
                    BitmapConverterSingleton.converter.fromBitmap(rotatedBitmap)
                selectedPhoto =
                    PhotoEntity(selectedPhoto!!.id, rotatedByteArray, selectedPhoto!!.activity)
            },
            onSave = {
                if (rotated or marked) {
                    photoViewModel.updatePhoto(selectedPhoto!!)
                    Toast.makeText(context, "Photo saved correctly", Toast.LENGTH_SHORT).show()
                    rotated = false
                } else selectedPhoto = null
            },
            onMark = { mark ->
                marked = true
                val markedBitmap = OpenCVUtils.markBitmap(bitmap, mark)
                val markedByteArray =
                    BitmapConverterSingleton.converter.fromBitmap(markedBitmap)
                selectedPhoto =
                    PhotoEntity(selectedPhoto!!.id, markedByteArray, selectedPhoto!!.activity)
            }
        )
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Photo:") },
            text = { Text("Are you sure you want to delete this photo?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        photoViewModel.deletePhoto(selectedPhoto!!)
                        showDeleteConfirmation = false
                        selectedPhoto = null
                    }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF383839)
                    ),
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PhotoGridItem(photo: PhotoEntity, onClick: () -> Unit) {
    val bitmap: ImageBitmap? = remember(photo.image) {
        photo.image?.let {
            BitmapConverterSingleton.converter.toBitmap(it).asImageBitmap()
        }
    }

    Image(
        bitmap = bitmap!!,
        contentDescription = "Stored Image",
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PhotoDialog(
    photo: PhotoEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onRotate: () -> Unit,
    onSave: () -> Unit,
    onMark: (String) -> Unit,
) {
    val bitmap: ImageBitmap? = remember(photo.image) {
        photo.image?.let {
            BitmapConverterSingleton.converter.toBitmap(it).asImageBitmap()
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(8.dp)
                .clickable(onClick = onDismiss),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC5C5C5),
                        contentColor = Color.DarkGray
                    ),
                    onClick = { onMark("Before") }
                ) {
                    Text(text = "Before")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC5C5C5),
                        contentColor = Color.DarkGray
                    ),
                    onClick = { onMark("During") }
                ) {
                    Text(text = "During")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC5C5C5),
                        contentColor = Color.DarkGray
                    ),
                    onClick = { onMark("After") }
                ) {
                    Text(text = "After")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16560B)
                    ),
                    onClick = onSave
                ) {
                    Text(text = "Save")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC95A1C)
                    ),
                    onClick = onRotate
                ) {
                    Icon(
                        Icons.Filled.Rotate90DegreesCw,
                        contentDescription = "Rotate",
                        tint = Color.White
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Full Screen Image",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Placeholder Image",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                onClick = onDelete
            ) {
                Text("Delete")
            }
        }
    }
}