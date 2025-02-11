package com.example.star.view.tools

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object OpenCVUtils {
    fun rotateBitmap90Clockwise(bitmap: Bitmap): Bitmap {
        // Convert Bitmap to OpenCV Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Rotate Mat 90 degrees clockwise
        Core.rotate(mat, mat, Core.ROTATE_90_CLOCKWISE)

        // Convert Mat back to Bitmap
        val rotatedBitmap = Bitmap.createBitmap(bitmap.height, bitmap.width, bitmap.config!!)
        Utils.matToBitmap(mat, rotatedBitmap)

        return rotatedBitmap
    }
    fun markBitmap(bitmap: Bitmap, text: String): Bitmap {
        // Convert Bitmap to OpenCV Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val imgWidth = bitmap.width.toDouble()
        val imgHeight = bitmap.height.toDouble()

        // Define dynamic properties
        val rectHeight = imgHeight / 12 // 1/12 of the image height
        val fontScale = rectHeight / 30 // Scale text size based on image height
        val thickness = (rectHeight / 10).toInt().coerceAtLeast(2) // Ensure visibility
        val padding = rectHeight / 6 // Padding inside rectangle
        val textPadding = rectHeight / 8 // Extra padding above the text

        val textColor =
            when (text) {
                "Before" -> Scalar(180.0, 0.0, 0.0) // Darker red
                "During" -> Scalar(30.0, 30.0, 30.0)
                "After" -> Scalar(0.0, 150.0, 0.0) // Darker green
                else -> Scalar(0.0, 0.0, 0.0) // Default black
            }
        val bgColor = Scalar(180.0, 180.0, 180.0) // Light gray background

        // Get text size
        val textSize = Imgproc.getTextSize(text.uppercase(), Imgproc.FONT_HERSHEY_SIMPLEX, fontScale, thickness, null)
        val textWidth = textSize.width
        val textHeight = textSize.height

        // Calculate position
        val rectTopLeft = Point(0.0, 0.0)
        val rectBottomRight = Point(imgWidth, rectHeight)
        val textPosition = Point((imgWidth - textWidth) / 2.0, (rectHeight + textHeight) / 2.0 - padding + textPadding)

        // Draw rectangle background
        Imgproc.rectangle(mat, rectTopLeft, rectBottomRight, bgColor, -1)

        // Draw text
        Imgproc.putText(mat, text.uppercase(), textPosition, Imgproc.FONT_HERSHEY_SIMPLEX, fontScale, textColor, thickness)

        // Convert Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, resultBitmap)

        return resultBitmap
    }

}