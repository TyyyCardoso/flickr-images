package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSizesModel
import retrofit2.Call
import retrofit2.http.GET

interface PhotoSizesService {
    @GET("?method=flickr.photos.getSizes&api_key=${Constants.APP_ID}&photo_id=7803551540&format=json&nojsoncallback=1")
    fun getInfo() : Call<PhotoSizesModel>
}