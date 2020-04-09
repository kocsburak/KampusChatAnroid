package com.xva.kampuschat.ui.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xva.kampuschat.R


class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {


    var list: ArrayList<String>? = null
    var inflater: LayoutInflater? = null
    var itemClickListener: ItemClickListener



    constructor(
        context: Context,
        texts: ArrayList<String>,
        itemClickListener: ItemClickListener
    ) {
        inflater = LayoutInflater.from(context)
        this.list = texts
        this.itemClickListener = itemClickListener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater!!.inflate(R.layout.recyclerview_settings_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val text = list!![position]
        holder.setData(text, position, itemClickListener)

    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var text: TextView = itemView.findViewById(R.id.textView2) as TextView

        fun setData(text: String, position: Int, clickListener: ItemClickListener) {

            this.text.text = text

            itemView.setOnClickListener {
                clickListener.onItemClick(position)
            }


        }

    }


    interface ItemClickListener {
        fun onItemClick(position: Int)
    }






}