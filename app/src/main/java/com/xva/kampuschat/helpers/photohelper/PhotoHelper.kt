package com.xva.kampuschat.helpers.photohelper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


class PhotoHelper {


    companion object {


        var bitmap: Bitmap? = null
        var url: String? = null

        public fun getBitmap(url: String): Bitmap? {



            var imageBytes = Base64.decode(url, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            return decodedImage

        }


        public fun getUrl(bitmap: Bitmap): String? {

            var byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            var imageBytes = byteArrayOutputStream.toByteArray()

            var image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            return image!!

        }







    }


}