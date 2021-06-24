package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * This interface uses GET method
 * to get Data from the API "flickr.photos.search"
 * with a "page" and "tags" query
 *
 */
interface PhotoSearchService {
    @GET("?method=flickr.photos.search&api_key=${Constants.APP_ID}&nojsoncallback=1&format=json")
    fun getInfo(
        @Query("page") page: Int,
        @Query("tags") tags: String
    ): Call<PhotoSearchModel>
}