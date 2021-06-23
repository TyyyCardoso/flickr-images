package azores.tyyy.cardoso.flickr_images.activities

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import azores.tyyy.cardoso.flickr_images.R
import com.github.florent37.materialimageloading.MaterialImageLoading
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_see_photo_big_size.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class SeePhotoBigSize : AppCompatActivity() {


    private var saveImageToInternalStorage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_photo_big_size)



        setSupportActionBar(toolbar_map)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Foto"

        toolbar_map.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra("url")) {
            //Log.i("WWT2", "${intent.getStringExtra("url")}")
            //Picasso.get().load(intent.getStringExtra("url")).into(imgBig)

            var url = intent.getStringExtra("url")
            var url2Name = url?.replace(".jpg", "")
            url2Name = url2Name?.replace("https://live.staticflickr.com/65535/", "")

            val imgFile: File =
                File("/data/user/0/azores.tyyy.cardoso.flickr_images/app_FlickrImages/${url2Name}.jpg")

            Log.i("WWTItem2", imgFile.toString())

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                imgBig.setImageBitmap(myBitmap)
            } else {
                Picasso.get().load(url)
                    .into(imgBig, object : Callback.EmptyCallback() {
                        override fun onSuccess() {
                            MaterialImageLoading.animate(imgBig).setDuration(3000).start()
                        }
                    })

                var drawable = imgBig.getDrawable() as BitmapDrawable
                var bitmap = drawable.bitmap
                Log.i("WWTItem", "${saveImageToInternalStorage(bitmap, url2Name!!)}")
            }

        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap, url2Name: String): Uri {

        val wrapper = ContextWrapper(applicationContext)


        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${url2Name}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()

            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val IMAGE_DIRECTORY = "FlickrImages"
    }

}