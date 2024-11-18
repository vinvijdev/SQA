package com.adiuxm.genaisqa.app.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object FileUtils {
    fun getFileFromUri(uri: Uri?, ctx: Context): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = ctx.contentResolver.query(uri!!, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath?.let { File(it) }
    }
}