package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSizesModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * This interface uses GET method
 * to get Data from the API "flickr.photos.getSizes"
 * with a "photo_id" query
 *
 */
interface PhotoSizesService {
    @GET("?method=flickr.photos.getSizes&api_key=${Constants.APP_ID}&format=json&nojsoncallback=1")
    fun getInfo(
        @Query("photo_id") photo_id: String,
    ): Call<PhotoSizesModel>
}