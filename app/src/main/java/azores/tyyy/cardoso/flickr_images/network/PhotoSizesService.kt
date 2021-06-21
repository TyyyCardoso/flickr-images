package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSizesModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoSizesService {
    @GET("?method=flickr.photos.getSizes&api_key=${Constants.APP_ID}&photo_id=51261914979&format=json&nojsoncallback=1")
    fun getInfo(
        @Query("photo_id") photo_id : String
    ) : Call<PhotoSizesModel>
}