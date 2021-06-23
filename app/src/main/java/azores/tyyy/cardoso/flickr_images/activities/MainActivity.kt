package azores.tyyy.cardoso.flickr_images.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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

    private lateinit var sharedPreferences: SharedPreferences

    var aux = 1

    var page = 1
    var isLoading = false
    var photoSearchList: PhotoSearchModel? = null
    var photoSizesList: PhotoSizesModel? = null

    var sp_check = false

    val list = ArrayList<String>()

    private val itemAdapter = ItemAdapter(this, list)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        itemAdapter.notifyDataSetChanged()

        Log.i("ONCRR", "create")


         if(Constants.isNetworkAvailable(this)){
             Constants.WAS_ONLINE = true
             searchTag(imgBtn)
         }

         else {
             Constants.WAS_OFFLINE = true
             do{
                 val sp = sharedPreferences.getString("$aux", "Error")
                 if(!sp.equals("Error"))
                    list.add(sp!!)
                 aux++
             }while(!sp.equals("Error"))
             itemAdapter.notifyDataSetChanged()
             sp_check = true
         }
        // Set the LayoutManager that this RecyclerView will use.
        rvItemsList.layoutManager = GridLayoutManager(this, 2)
        // Adapter class is initialized and list is passed in the param.
        // adapter instance is set to the recyclerview to inflate the items.
        rvItemsList.adapter = itemAdapter

        rvItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(Constants.isNetworkAvailable(this@MainActivity) && Constants.WAS_OFFLINE){
                    Log.i("WWTt", "test")
                    sp_check = false
                    list.clear()
                    itemAdapter.notifyDataSetChanged()
                    Constants.WAS_OFFLINE = false
                }
                if(!Constants.isNetworkAvailable(this@MainActivity)){
                    aux = 1
                    Constants.WAS_OFFLINE = true
                    if(!sp_check){
                        Constants.WAS_ONLINE = false
                        list.clear()
                        do{
                            val sp = sharedPreferences.getString("$aux", "Error")
                            if(!sp.equals("Error"))
                                list.add(sp!!)
                            aux++
                        }while(!sp.equals("Error"))
                        itemAdapter.notifyDataSetChanged()
                        Log.i("OFFON", "P1")
                        sp_check = true
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

    private fun getPhotoSearch(tag : String = "ocean") {
        isLoading = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PhotoSearchService =
            retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        Log.i("PAGE", "$page")
        val listCall: Call<PhotoSearchModel> = service.getInfo(page, tag)


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

    private fun getBitmapFromURL(string : String) {
        Picasso.get().load(string).into(object: com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.i("ERROR", "ERROR - $string")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.i("LOADED", "LOADED - $string")
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Log.i("BITMAP", "$aux - $bitmap")
                val editor = sharedPreferences.edit()
                editor.putString("$aux", Utils.BitmapToBASE64(bitmap))
                Log.i("EDITOR", "$aux - ${Utils.BitmapToBASE64(bitmap)}")
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
