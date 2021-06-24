package azores.tyyy.cardoso.flickr_images.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


object Utils {
    /**
     *
     * This function receives a bitmap
     * converts it to a byteArray
     * and then return it as a Base64 String
     *
     */
    fun BitmapToBASE64(bitmap: Bitmap?): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     *
     * This function receives a string
     * converts it to a byteArray
     * and then return it as a bitmap
     *
     */
    fun BASE64ToBitmap(image: String): Bitmap? {
        val imageByte: ByteArray = Base64.decode(image, Base64.NO_WRAP)
        val inputStream: InputStream = ByteArrayInputStream(imageByte)
        val bmp = BitmapFactory.decodeStream(inputStream)
        if (bmp != null)
            return bmp
        else
            return null
    }
}