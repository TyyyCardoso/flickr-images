package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import azores.tyyy.cardoso.flickr_images.models.PhotoSizesModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoSearchService {
    @GET("?method=flickr.photos.search&api_key=${Constants.APP_ID}&nojsoncallback=1&format=json")
    fun getInfo(
        @Query("page") page: Int,
        @Query("tags") tags: String
    ): Call<PhotoSearchModel>
}