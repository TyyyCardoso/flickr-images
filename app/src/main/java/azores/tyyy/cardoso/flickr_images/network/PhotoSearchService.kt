package azores.tyyy.cardoso.flickr_images.network

import azores.tyyy.cardoso.flickr_images.models.PhotoSearchModel
import retrofit2.Call
import retrofit2.http.GET

interface PhotoSearchService {
    @GET("?method=flickr.photos.search&api_key=7bdb03d29144dbbabc9c71fd173ac356&tags=bird&nojsoncallback=1&format=json")
    fun getInfo() : Call<PhotoSearchModel>
}