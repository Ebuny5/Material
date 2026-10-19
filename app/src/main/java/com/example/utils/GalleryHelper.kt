package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object GalleryHelper {

    fun populateEmulatorGallery(context: Context): Int {
        val samples = listOf(
            Pair("peltier_cooling_glove_schematic.jpg", R.drawable.cooling_glove_sample),
            Pair("iontophoresis_medical_schematic.jpg", R.drawable.iontophoresis_sample),
            Pair("tens_biostimulator_schematic.jpg", R.drawable.tens_sample),
            Pair("bioimpedance_sensor_schematic.jpg", R.drawable.bioimpedance_sample),
            Pair("neck_cooler_schematic.jpg", R.drawable.neck_cooler_sample),
            Pair("sweat_patch_schematic.jpg", R.drawable.sweat_patch_sample)
        )

        var count = 0
        for ((name, resId) in samples) {
            try {
                val bitmap = BitmapFactory.decodeResource(context.resources, resId) ?: continue
                saveBitmapToPictures(context, bitmap, name)
                count++
            } catch (e: Exception) {
                // Continue saving others
            }
        }
        return count
    }

    private fun saveBitmapToPictures(context: Context, bitmap: Bitmap, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WearableHardware")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out: OutputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }
    }
}
