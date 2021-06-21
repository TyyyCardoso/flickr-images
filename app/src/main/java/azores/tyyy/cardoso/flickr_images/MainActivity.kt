package azores.tyyy.cardoso.flickr_images

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import azores.tyyy.cardoso.flickr_images.network.PhotoSearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData()
    }

    private fun getData(){
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service : PhotoSearchService = retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        val listCall : Call<PhotoSearchModel> = service.getInfo()

        listCall.enqueue(object : Callback<PhotoSearchModel> {
            override fun onResponse(call: Call<PhotoSearchModel>, response: Response<PhotoSearchModel>) {
                if(response!!.isSuccessful){
                    val testList : PhotoSearchModel? = response.body()
                    Log.i("WWT", "$testList")
                } else {
                    Log.i("WWT", response.code().toString())
                }
            }

            override fun onFailure(call: Call<PhotoSearchModel>, t: Throwable) {
                Log.i("WWT", t!!.message.toString())
            }

        })
    }
}