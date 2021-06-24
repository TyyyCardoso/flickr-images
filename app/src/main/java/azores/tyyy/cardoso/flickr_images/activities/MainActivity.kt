package azores.tyyy.cardoso.flickr_images.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import azores.tyyy.cardoso.flickr_images.utils.Validation
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

        //if has internet search for new images
        if(Constants.isNetworkAvailable(this)) {
            searchTag(imgBtn)
        } else {
            //if has no internet load images saved in sharedPreferences
            sharedPreferencesInput()
            WAS_OFFLINE = true
            SP_CHECK = true

        }

        //configuration of recycler view
        rvItemsList.layoutManager = GridLayoutManager(this, 2)
        rvItemsList.adapter = itemAdapter

        rvItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                //check if internet is turn to on
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

                //if scroll is detected (dy > 0)
                if (dy > 0) {
                    val visibleItemCount = rvItemsList.childCount
                    val pastVisibleItem =
                        (rvItemsList.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                    val total = itemAdapter.itemCount

                    if (!isLoading) {
                        //check if there are more items below
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

    /**
     *
     * This method get the sharedPreferences
     * and adds it to the recycler view list
     *
     */
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

    /**
     * Function is used to get photo details
     */
    private fun getPhotoSearch(tag : String = "ocean") {
        isLoading = true
        /**
         * Add the built-in converter factory first. This prevents overriding its
         * behavior but also ensures correct behavior when using converters that consume all types.
         */
        val retrofit: Retrofit = Retrofit.Builder()
            // API base URL.
            .baseUrl(Constants.BASE_URL)
            /** Add converter factory for serialization and deserialization of objects. */
            /**
             * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
             * decoding from JSON (when no charset is specified by a header) will use UTF-8.
             */
            .addConverterFactory(GsonConverterFactory.create())
            /** Create the Retrofit instances. */
            .build()

        /**
         * Here we map the service interface in which we declares the end point and the API type
         *i.e GET, POST and so on along with the request parameter which are required.
         */
        val service: PhotoSearchService =
            retrofit.create<PhotoSearchService>(PhotoSearchService::class.java)

        /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
         * Here we pass the required param in the service
         */
        val listCall: Call<PhotoSearchModel> = service.getInfo(page, tag)

        // Callback methods are executed using the Retrofit callback executor.
        listCall.enqueue(object : Callback<PhotoSearchModel> {
            override fun onResponse(
                call: Call<PhotoSearchModel>,
                response: Response<PhotoSearchModel>
            ) {
                // Check the response is success or not.
                if (response!!.isSuccessful) {
                    photoSearchList = response.body()
                    getSizesSearch()
                }
            }

            override fun onFailure(call: Call<PhotoSearchModel>, t: Throwable) {
            }

        })
    }

    /**
     * Function is used to get photos
     */
    private fun getSizesSearch() {
        /**
         * Add the built-in converter factory first. This prevents overriding its
         * behavior but also ensures correct behavior when using converters that consume all types.
         */
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            /** Add converter factory for serialization and deserialization of objects. */
            /**
             * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
             * decoding from JSON (when no charset is specified by a header) will use UTF-8.
             */
            .addConverterFactory(GsonConverterFactory.create())
            /** Create the Retrofit instances. */
            .build()

        /**
         * Here we map the service interface in which we declares the end point and the API type
         *i.e GET, POST and so on along with the request parameter which are required.
         */
        val service: PhotoSizesService =
            retrofit.create<PhotoSizesService>(PhotoSizesService::class.java)

        for (item in photoSearchList!!.photos.photo) {

            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
             * Here we pass the required param in the service
             */
            val listCall: Call<PhotoSizesModel> = service.getInfo(item.id)

            listCall.enqueue(object : Callback<PhotoSizesModel> {
                override fun onResponse(
                    call: Call<PhotoSizesModel>,
                    response: Response<PhotoSizesModel>
                ) {
                    if (response!!.isSuccessful) {
                        photoSizesList = response.body()
                        //add photos url to recycler view data list
                        list.add(photoSizesList!!.sizes.size[1].source)
                        //get bitmap from url and saves it in sharedPreferences
                        getBitmapFromURL(photoSizesList!!.sizes.size[1].source)

                        itemAdapter.notifyDataSetChanged()

                        itemAdapter.setOnClickListener(object :
                            ItemAdapter.OnClickListener {
                            //View photo in bigger size
                            override fun onClick(position: Int, model: String) {
                                if(Constants.isNetworkAvailable(this@MainActivity)){
                                    val intent = Intent(this@MainActivity, SeePhotoBigSize::class.java)
                                    /**
                                     *
                                     * for the same photo there are different sizes
                                     * but all photos have a unique ID
                                     * so the only difference in the URl is the character which reference the image size
                                     * EXAMPLE:
                                     * https://live.staticflickr.com/8424/7803551540_8c6211646e_b.jpg
                                     * https://live.staticflickr.com/8424/7803551540_8c6211646e_q.jpg
                                     * so we use replace built in function to change the url from "..._q" to "..._b"
                                     *
                                     */
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

    /**
     *
     * Receives a URL as a string
     * converts it to a Bitmap
     * and saves it in sharedPreferences
     *
     */
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

    /**
     *
     * Check if has internet
     * and gets tags from EditText
     * then sends it to getPhotoSearch method
     *
     */
    fun searchTag(view: View) {
        hideSoftKeyboard(imgBtn)
        if (Constants.isNetworkAvailable(this@MainActivity)) {
            var tag = tag_name.text.toString()
            list.clear()
            itemAdapter.notifyDataSetChanged()
            if(Validation.searchTagValidation(tag)){
                if (tag.isNotEmpty())
                    getPhotoSearch(tag)
                else
                    getPhotoSearch()

            }else{
                tag_name.error = "Your tag must be more than 2 and less than 11 characters and have no punctuation"
                Toast.makeText(
                    this@MainActivity,
                    "Insert a valid tag",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "No Internet Connection available",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    /**
     *
     * Restarts the activity
     *
     */
    fun refresh(view: View) {
        val intent = intent
        finish()
        startActivity(intent)
        overridePendingTransition(0, 0);
    }

    /**
     *
     * Hide Keyboard for better aspect
     *
     */
    fun hideSoftKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
