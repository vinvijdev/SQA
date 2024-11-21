package com.adiuxm.genaisqa.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

object BitmapUtils {
    fun base64toBitmap(base64String: String?): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convertImageUriToBase64(context: Context, uri: Uri): String? {
        return try {
            // Get the InputStream from the URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            // Convert InputStream to Bitmap
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            // Compress the Bitmap to a ByteArrayOutputStream
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Convert ByteArray to Base64 String
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}