package com.xva.kampuschat.ui.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.helpers.photohelper.PhotoHelper

class ListsAdapter : RecyclerView.Adapter<ListsAdapter.MyViewHolder> {



    var list: ArrayList<Profile>? = null
    var inflater: LayoutInflater? = null
    var itemClickListener: ItemClickListener



    constructor(
        context: Context,
        profiles: ArrayList<Profile>,
        itemClickListener: ItemClickListener
    ) {
        inflater = LayoutInflater.from(context)
        this.list = profiles
        this.itemClickListener = itemClickListener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater!!.inflate(R.layout.recyclerview_lists_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val profile = list!![position]
        holder.setData(profile, position, itemClickListener)

    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.Fullname) as TextView
        var department: TextView = itemView.findViewById(R.id.Deparment) as TextView
        var pp: ImageView = itemView.findViewById(R.id.ProfilePhoto) as ImageView
        var settings  = itemView.findViewById<ImageView>(R.id.ItemSettings) as ImageView

        fun setData(profile: Profile, position: Int, clickListener: ItemClickListener) {

            this.name.text = profile.fullname
            this.department.text = profile.department_name


            if(profile.profile_photo_url != null && profile.profile_photo_url != "" && profile.profile_photo_url != "forbidden"){
                pp.setImageBitmap(PhotoHelper.getBitmap(profile.profile_photo_url!!))
            }


            settings.setOnClickListener {
                clickListener.onItemClick(settings,position)
            }

            itemView.setOnClickListener {
                clickListener.onItemClick(itemView, position)
            }


        }

    }


    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }




}