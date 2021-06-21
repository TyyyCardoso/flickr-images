package azores.tyyy.cardoso.flickr_images.constants

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {
    //API KEY
    const val APP_ID: String = "baaca7335fd6c06d8ed8e094ee6f642b"
    const val BASE_URL : String = "https://api.flickr.com/services/rest/"



    fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.
        getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }




    }
}