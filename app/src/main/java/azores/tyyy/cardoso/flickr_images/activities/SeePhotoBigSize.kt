package azores.tyyy.cardoso.flickr_images.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import azores.tyyy.cardoso.flickr_images.R
import com.github.florent37.materialimageloading.MaterialImageLoading
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_see_photo_big_size.*


class SeePhotoBigSize : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_photo_big_size)

        /**
         *
         * Define a toolbar with back button
         *
         */
        setSupportActionBar(toolbar_map)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Foto"

        toolbar_map.setNavigationOnClickListener {
            onBackPressed()
        }

        //if has internet, the image is loaded from url with picasso library
        if (intent.hasExtra("url")) {
            Picasso.get().load(intent.getStringExtra("url"))
                .into(imgBig, object : Callback.EmptyCallback() {
                    override fun onSuccess() {
                        MaterialImageLoading.animate(imgBig).setDuration(2000).start()
                    }
                })
        }
    }
}

