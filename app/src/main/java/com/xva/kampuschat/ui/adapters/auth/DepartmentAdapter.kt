package com.xva.kampuschat.ui.adapters.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.auth.Department


class DepartmentAdapter : RecyclerView.Adapter<DepartmentAdapter.MyViewHolder> {


    var list: List<Department>? = null
    var inflater: LayoutInflater? = null
    var itemClickListener: ItemClickListener


    constructor(context: Context, departments: List<Department>, itemClickListener: ItemClickListener) {
        inflater = LayoutInflater.from(context)
        this.list = departments
        this.itemClickListener = itemClickListener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater!!.inflate(R.layout.item_university, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val department = list!![position]
        holder.setData(department,position,itemClickListener)

    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {

        var name: TextView = itemView.findViewById(R.id.textViewName) as TextView


        fun setData(department: Department, position: Int, clickListener: ItemClickListener) {

            this.name.text = department.name
            itemView.setOnClickListener{
                clickListener.onItemClick(itemView,position)
            }
        }

    }


    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


}