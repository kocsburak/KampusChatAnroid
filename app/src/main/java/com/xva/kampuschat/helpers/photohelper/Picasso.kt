package com.xva.kampuschat.helpers.photohelper

import android.widget.ImageView
import com.squareup.picasso.Picasso

class Picasso {


    companion object{

        fun loadPhoto(url:String,imageView:ImageView){
            Picasso.get()
                .load(url)
                .into(imageView)
        }
    }



}