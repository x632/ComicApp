package com.poema.comicapp.data_sources.local.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes!=null){
            BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        }else null

    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap?) : ByteArray?{
        return if(bmp!=null){
            val outputStream = ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.PNG,100, outputStream)
            outputStream.toByteArray()
        }else null
    }
}