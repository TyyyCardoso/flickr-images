package azores.tyyy.cardoso.flickr_images.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import azores.tyyy.cardoso.flickr_images.R
import azores.tyyy.cardoso.flickr_images.constants.Constants
import azores.tyyy.cardoso.flickr_images.utils.Utils
import com.github.florent37.materialimageloading.MaterialImageLoading
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.items_row.view.*
import java.io.ByteArrayInputStream
import java.io.InputStream


class ItemAdapter(val context: Context, val items: ArrayList<String>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_row,
                parent,
                false
            )
        )

    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if(Constants.isNetworkAvailable(context)){
            Picasso.get().load(item).into(holder.tvItem, object : Callback.EmptyCallback(){
                override fun onSuccess() {
                    MaterialImageLoading.animate(holder.tvItem).setDuration(1500).start()
                }
            } )
        }else{
            holder.tvItem.setImageBitmap(Utils.BASE64ToBitmap(item))
        }

        
        holder.tvItem.setOnClickListener {
            //Log.i("WWT", "${item}")

            onClickListener = onClickListener

            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
                Log.i("WWT_PhotoClick", "${item}")

            }
        }

    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        val tvItem = view.tvItem


    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: String)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}