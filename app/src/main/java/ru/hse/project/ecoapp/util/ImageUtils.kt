package ru.hse.project.ecoapp.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageUtils {
    companion object {
        fun getUrlFromUri(context: Context, uri: Uri): String {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(uri, proj, null, null, null)!!
                val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(column_index)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }


        @Throws(IOException::class)
        fun createTempFile(context: Context): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir: File = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )


            val image: File = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",  // suffix
                storageDir // directory
            )


            return image
        }


        private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            return bitmap
        }


        fun getBitmap(context: Context, drawableId: Int): Bitmap {
            return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
                is BitmapDrawable -> drawable.bitmap

                is VectorDrawable -> getBitmap(drawable)

                else -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            }
        }



    }
}