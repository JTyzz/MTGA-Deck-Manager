package com.earthdefensesystem.retrorv.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileInputStream

class ImageStoreManager {
    companion object {

        fun saveToInternalStorage(context: Context, bitmapImg: Bitmap, imageFileName: String): String{
            context.openFileOutput(imageFileName, Context.MODE_PRIVATE).use {fos->
                bitmapImg.compress(Bitmap.CompressFormat.PNG,25, fos)
            }
            Log.d("debug", "Saved image to ${context.filesDir.absolutePath}")
            return context.filesDir.absolutePath
        }

        fun getImageFromInternalStorage(context: Context, imageFileName: String): Bitmap?{
            val directory = context.filesDir
            val file = File(directory, imageFileName)
            return BitmapFactory.decodeStream(FileInputStream(file))
        }

        fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean{
            val directory = context.filesDir
            val file = File(directory, imageFileName)
            return file.delete()
        }
    }
}