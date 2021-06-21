package azores.tyyy.cardoso.flickr_images

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import azores.tyyy.cardoso.flickr_images.adapter.ItemAdapter
import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.*
import azores.tyyy.cardoso.flickr_images.network.PhotoSearchService
import azores.tyyy.cardoso.flickr_images.network.PhotoSizesService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {

    var photoSearchList : PhotoSearchModel? = null
    var photoSizesList : PhotoSizesModel? = null

    val list = ArrayList<String>()

    private val itemAdapter = ItemAdapter(this, list)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPhotoSearch()

        // Set the LayoutManager that this RecyclerView will use.
        rvItemsList.layoutManager = GridLayoutManager(this, 2)
        // Adapter class is initialized and list is passed in the param.
        // adapter instance is set to the recyclerview to inflate the items.
        rvItemsList.adapter = itemAdapter
    }

    private fun getPhotoSearch(){
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service : PhotoSearchService = retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        val listCall : Call<PhotoSearchModel> = service.getInfo()

        listCall.enqueue(object : Callback<PhotoSearchModel> {
            override fun onResponse(call: Call<PhotoSearchModel>, response: Response<PhotoSearchModel>) {
                if(response!!.isSuccessful){
                    photoSearchList = response.body()
                    Log.i("WWT", "$photoSearchList")
                    getSizesSearch()
                } else {
                    Log.i("WWT", response.code().toString())
                }
            }

            override fun onFailure(call: Call<PhotoSearchModel>, t: Throwable) {
                Log.i("WWT", t!!.message.toString())
            }

        })
    }

    private fun getSizesSearch(){


        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service : PhotoSizesService = retrofit.create<PhotoSizesService>(PhotoSizesService::class.java)

        for(item in photoSearchList!!.photos.photo){

            val listCall : Call<PhotoSizesModel> = service.getInfo(item.id)

            listCall.enqueue(object : Callback<PhotoSizesModel> {
                override fun onResponse(call: Call<PhotoSizesModel>, response: Response<PhotoSizesModel>) {
                    if(response!!.isSuccessful){
                        photoSizesList = response.body()
                        list.add(photoSizesList!!.sizes.size[1].source)
                        itemAdapter.notifyDataSetChanged()
                    } else {
                        Log.i("WWTe", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<PhotoSizesModel>, t: Throwable) {
                    Log.i("WWTd", t!!.message.toString())
                }

            })
        }
    }
}