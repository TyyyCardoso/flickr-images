package azores.tyyy.cardoso.flickr_images

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import azores.tyyy.cardoso.flickr_images.models.PhotoSizesModel
import azores.tyyy.cardoso.flickr_images.network.PhotoSearchService
import azores.tyyy.cardoso.flickr_images.network.PhotoSizesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {

    var photoSearchList : PhotoSearchModel? = null
    var photoSizesList : PhotoSizesModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPhotoSearch()
        getSizesSearch()
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

        val listCall : Call<PhotoSizesModel> = service.getInfo()

        listCall.enqueue(object : Callback<PhotoSizesModel> {
            override fun onResponse(call: Call<PhotoSizesModel>, response: Response<PhotoSizesModel>) {
                if(response!!.isSuccessful){
                    photoSizesList = response.body()
                    Log.i("WWT", "$photoSizesList")
                } else {
                    Log.i("WWT", response.code().toString())
                }
            }

            override fun onFailure(call: Call<PhotoSizesModel>, t: Throwable) {
                Log.i("WWT", t!!.message.toString())
            }

        })
    }
}