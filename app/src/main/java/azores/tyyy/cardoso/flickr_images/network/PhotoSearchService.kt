package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import retrofit2.Call
import retrofit2.http.GET

interface PhotoSearchService {
    @GET("?method=flickr.photos.search&api_key=${Constants.APP_ID}&tags=bird&nojsoncallback=1&format=json")
    fun getInfo() : Call<PhotoSearchModel>
}