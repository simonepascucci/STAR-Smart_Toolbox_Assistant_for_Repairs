package com.example.star.view.tools

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.star.viewmodel.ActivityViewModel
import com.example.star.viewmodel.PhotoViewModel

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("SimpleDateFormat")
@Composable
fun PhotosPage(activityViewModel: ActivityViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showEditOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val photoViewModel = remember { PhotoViewModel(context) }
    val photos = photoViewModel.photos.observeAsState()
    val activityId = activityViewModel.selectedActivity.value!!.name

    LaunchedEffect (Unit){
        photoViewModel.fetchPhotos(activityId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (photos.value != null) {
            if (photos.value!!.isEmpty()) {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(text = "No photos found for: ${activityId}.", fontSize = 18.sp, fontWeight = FontWeight.W500)
                }
            }
            else{
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "${activityId}:", fontSize = 18.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    DisplayPhotos(modifier = Modifier, photos = photos.value!!, photoViewModel)
                }
            }
        }else{
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = Color(0xFFD25D1C),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.AddToPhotos, "Add", tint = Color.White)
        }
        if (showDialog) {
            ImagePicker(
                onDismiss = { showDialog = false },
                onImageCaptured = { uri ->
                    capturedImageUri = uri
                    showDialog = false
                    showImageDialog = true
                    showEditOptions = false
                }
            )
        }

        if (showImageDialog && capturedImageUri != null) {
            FullScreenImageDialog(
                modifier = Modifier.padding(24.dp),
                photoUri = capturedImageUri!!,
                onCancel = { showImageDialog = false; capturedImageUri = null },
                onUpload = {
                    showImageDialog = false
                    bitmap = uriToBitmapSync(context = context, uri = capturedImageUri!!)
                    capturedImageUri = null
                    val rotatedBitMap = OpenCVUtils.rotateBitmap90Clockwise(bitmap!!)
                    photoViewModel.insertPhoto(rotatedBitMap, activityId)
                           }
            )
        }
    }
}

@Composable
fun FullScreenImageDialog(
    modifier: Modifier,
    photoUri: Uri,
    onCancel: () -> Unit,
    onUpload: () -> Unit,
) {

    Dialog(
        onDismissRequest = onCancel,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF363737)), // Dialog Background Color
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                val painter = rememberAsyncImagePainter(model = photoUri)
                Image(
                    painter = painter,
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload Button (Green)
                Button(
                    onClick = onUpload,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16590B))
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = "Upload")
                    Spacer(Modifier.width(4.dp))
                    Text("Upload")
                }

                Spacer(modifier = Modifier.height(8.dp)) // Add space between buttons

                // Cancel Button (Red)
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Cancel")
                    Spacer(Modifier.width(4.dp))
                    Text("Cancel")
                }
            }
        }
    }
}


