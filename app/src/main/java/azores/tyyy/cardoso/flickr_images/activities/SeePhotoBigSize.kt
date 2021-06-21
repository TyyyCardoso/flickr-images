package azores.tyyy.cardoso.flickr_images.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import azores.tyyy.cardoso.flickr_images.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_see_photo_big_size.*

class SeePhotoBigSize : AppCompatActivity() {
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
            Picasso.get().load(intent.getStringExtra("url")).into(imgBig)
        }
    }
}