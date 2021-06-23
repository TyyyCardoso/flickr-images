package azores.tyyy.cardoso.flickr_images.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
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
import azores.tyyy.cardoso.flickr_images.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_see_photo_big_size.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private var WAS_OFFLINE = false

    private lateinit var sharedPreferences: SharedPreferences

    private var aux = 1

    private var page = 1
    private var isLoading = false

    private var photoSearchList: PhotoSearchModel? = null
    private var photoSizesList: PhotoSizesModel? = null

    private var SP_CHECK = false

    private val list = ArrayList<String>()

    private val itemAdapter = ItemAdapter(this, list)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        if(Constants.isNetworkAvailable(this)) {
            searchTag(imgBtn)
        } else {
            sharedPreferencesInput()
            WAS_OFFLINE = true
            SP_CHECK = true

        }

        rvItemsList.layoutManager = GridLayoutManager(this, 2)
        rvItemsList.adapter = itemAdapter

        rvItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(Constants.isNetworkAvailable(this@MainActivity) && WAS_OFFLINE){
                    SP_CHECK = false
                    list.clear()
                    itemAdapter.notifyDataSetChanged()
                    WAS_OFFLINE = false
                }
                if(!Constants.isNetworkAvailable(this@MainActivity)){
                    WAS_OFFLINE = true
                    if(!SP_CHECK){
                        list.clear()
                        sharedPreferencesInput()
                        SP_CHECK = true
                    }
                }

                if (dy > 0) {
                    val visibleItemCount = rvItemsList.childCount
                    val pastVisibleItem =
                        (rvItemsList.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                    val total = itemAdapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            page++
                            if(Constants.isNetworkAvailable(this@MainActivity))
                                if(tag_name.text.toString().isNotEmpty())
                                    getPhotoSearch(tag_name.text.toString())
                                else
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

    private fun sharedPreferencesInput(aux : Int = 1){
        var localAux = aux
        do{
            val sp = sharedPreferences.getString("$localAux", "Error")
            if(!sp.equals("Error"))
                list.add(sp!!)
            localAux++
        }while(!sp.equals("Error"))
        itemAdapter.notifyDataSetChanged()
    }

    private fun getPhotoSearch(tag : String = "ocean") {
        isLoading = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PhotoSearchService =
            retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        val listCall: Call<PhotoSearchModel> = service.getInfo(page, tag)


        listCall.enqueue(object : Callback<PhotoSearchModel> {
            override fun onResponse(
                call: Call<PhotoSearchModel>,
                response: Response<PhotoSearchModel>
            ) {
                if (response!!.isSuccessful) {
                    photoSearchList = response.body()
                    getSizesSearch()
                }
            }

            override fun onFailure(call: Call<PhotoSearchModel>, t: Throwable) {
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
                        getBitmapFromURL(photoSizesList!!.sizes.size[1].source)
                        itemAdapter.notifyDataSetChanged()

                        itemAdapter.setOnClickListener(object :
                            ItemAdapter.OnClickListener {
                            override fun onClick(position: Int, model: String) {
                                if(Constants.isNetworkAvailable(this@MainActivity)){
                                    val intent = Intent(this@MainActivity, SeePhotoBigSize::class.java)
                                    var modelFix = model.replace("_q","_b")
                                    intent.putExtra("url", modelFix)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@MainActivity, "No Internet Connection available", Toast.LENGTH_SHORT).show()
                                }

                            }
                        })

                    }
                }

                override fun onFailure(call: Call<PhotoSizesModel>, t: Throwable) {
                }

            })
        }
        isLoading = false

    }

    private fun getBitmapFromURL(string : String) {
        Picasso.get().load(string).into(object: com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val editor = sharedPreferences.edit()
                editor.putString("$aux", Utils.BitmapToBASE64(bitmap))
                editor.apply()
                aux++
            }
        })


    }

    fun searchTag(view: View){
        if(Constants.isNetworkAvailable(this@MainActivity)){
            var tag = tag_name.text.toString()
            list.clear()
            itemAdapter.notifyDataSetChanged()
            if(!tag.isEmpty())
                getPhotoSearch(tag)
            else{
                getPhotoSearch()
            }
        }
        else {
            Toast.makeText(this@MainActivity, "No Internet Connection available", Toast.LENGTH_SHORT).show()
        }

    }

    fun refresh(view: View) {
        val intent = intent
        finish()
        startActivity(intent)
        overridePendingTransition(0, 0);
    }
}
