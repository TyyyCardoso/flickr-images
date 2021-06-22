package azores.tyyy.cardoso.flickr_images.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azores.tyyy.cardoso.flickr_images.R
import azores.tyyy.cardoso.flickr_images.adapter.ItemAdapter
import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.*
import azores.tyyy.cardoso.flickr_images.network.PhotoSearchService
import azores.tyyy.cardoso.flickr_images.network.PhotoSizesService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var customProgressDialog: Dialog

    var page = 1
    var isLoading = false
    var photoSearchList: PhotoSearchModel? = null
    var photoSizesList: PhotoSizesModel? = null

    val list = ArrayList<String>()

    private val itemAdapter = ItemAdapter(this, list)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


         if(Constants.isNetworkAvailable(this))
            getPhotoSearch()
        else
            Toast.makeText(this, "No Internet Connection available", Toast.LENGTH_SHORT).show()

        // Set the LayoutManager that this RecyclerView will use.
        rvItemsList.layoutManager = GridLayoutManager(this, 2)
        // Adapter class is initialized and list is passed in the param.
        // adapter instance is set to the recyclerview to inflate the items.
        rvItemsList.adapter = itemAdapter

        rvItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0) {
                    val visibleItemCount = rvItemsList.childCount
                    val pastVisibleItem =
                        (rvItemsList.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                    val total = itemAdapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            page++
                            if(Constants.isNetworkAvailable(this@MainActivity))
                                getPhotoSearch()
                            else
                                Toast.makeText(this@MainActivity, "No Internet Connection available", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                super.onScrolled(recyclerView, dx, dy)

            }
        })


    }

    private fun getPhotoSearch() {
        isLoading = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PhotoSearchService =
            retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        val listCall: Call<PhotoSearchModel> = service.getInfo(page)


        listCall.enqueue(object : Callback<PhotoSearchModel> {
            override fun onResponse(
                call: Call<PhotoSearchModel>,
                response: Response<PhotoSearchModel>
            ) {
                if (response!!.isSuccessful) {
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

    private fun getSizesSearch() {


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PhotoSizesService =
            retrofit.create<PhotoSizesService>(PhotoSizesService::class.java)

        for (item in photoSearchList!!.photos.photo) {

            val listCall: Call<PhotoSizesModel> = service.getInfo(item.id)

            listCall.enqueue(object : Callback<PhotoSizesModel> {
                override fun onResponse(
                    call: Call<PhotoSizesModel>,
                    response: Response<PhotoSizesModel>
                ) {
                    if (response!!.isSuccessful) {

                        photoSizesList = response.body()
                        list.add(photoSizesList!!.sizes.size[1].source)
                        itemAdapter.notifyDataSetChanged()

                        itemAdapter.setOnClickListener(object :
                            ItemAdapter.OnClickListener {
                            override fun onClick(position: Int, model: String) {
                                val intent = Intent(this@MainActivity, SeePhotoBigSize::class.java)
                                var modelFix = model.replace("_q","_b")
                                intent.putExtra("url", modelFix)
                                startActivity(intent)
                            }
                        })

                    } else {
                        Log.i("WWTe", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<PhotoSizesModel>, t: Throwable) {
                    Log.i("WWTd", t!!.message.toString())
                }

            })
        }
        isLoading = false

    }

}
