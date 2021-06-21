package azores.tyyy.cardoso.flickr_images.models

import java.io.Serializable

data class PhotoSearchModel(
    val photos: Photos,
    val stat: String
) : Serializable {
    data class Photos(
        val page: Int,
        val pages: Int,
        val perpage: Int,
        val photo: List<Photo>,
        val total: Int
 ) : Serializable {
        data class Photo(
            val farm: Int,
            val id: String,
            val isfamily: Int,
            val isfriend: Int,
            val ispublic: Int,
            val owner: String,
            val secret: String,
            val server: String,
            val title: String
        ) : Serializable
    }
}