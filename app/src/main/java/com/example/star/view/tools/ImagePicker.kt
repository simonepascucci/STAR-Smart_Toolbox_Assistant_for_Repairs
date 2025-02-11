package com.example.star.view.tools

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
@Composable
fun ImagePicker(
    onDismiss: () -> Unit,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && capturedImageUri != null) {
                onImageCaptured(capturedImageUri!!)
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                capturedImageUri = uri
                onImageCaptured(uri)  // Ensure the gallery image is also handled
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Photo") },
        text = { Text("Choose how you want to upload a photo.") },
        confirmButton = {
            OutlinedButton(onClick = {
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val fileName = "JPEG_${timeStamp}_"
                val file = File.createTempFile(fileName, ".jpg", context.cacheDir)
                val uri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                capturedImageUri = uri
                cameraLauncher.launch(uri)
            }) {
                Text("Take a picture")
                Icon(Icons.Filled.CameraAlt, contentDescription = "camera")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Select from gallery")
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "gallery")
            }
        }
    )
}

fun uriToBitmapSync(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
