package com.xva.kampuschat.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso

class PicassoHelper {


    companion object{

        fun loadPhoto(url:String,imageView:ImageView){
            Picasso.get()
                .load(url)
                .into(imageView)
        }
    }



}