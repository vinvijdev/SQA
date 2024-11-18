package com.adiuxm.genaisqa.app.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object LocationUtils {

    fun isLocationPermissionGranted(context: Context): Boolean {
        return true
        return (ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}