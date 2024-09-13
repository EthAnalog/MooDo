package com.example.moodo.db

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object MooDoUtils {
    fun getFile(context: Context, uri: Uri): File {
        val filePath = getPath(context, uri)
        return File(filePath!!)
    }

    private fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(columnIndex!!)
    }
}