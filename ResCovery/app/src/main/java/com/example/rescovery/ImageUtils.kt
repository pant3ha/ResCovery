package com.example.rescovery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

// Code from https://stackoverflow.com/questions/4830711/how-can-i-convert-an-image-into-a-base64-string

object ImageUtils {

    // Encode Image uri to Base64 string
    fun encode(context: Context, imgUri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(imgUri)
            val bitmap = BitmapFactory.decodeStream(input)

            // Encode image to base64 string
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            var imageBytes = output.toByteArray()

            // Encode ByteArray to Base64
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Decode Base64 string to Bitmap
    fun decode(base64String: String): Bitmap? {
        return try {
            if (base64String.isEmpty()) {
                Log.e("ImageUtils", "Base64 string is empty or null")
                return null
            }
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Failed to decode image", e)
            return null
        }
    }
}