package com.xva.kampuschat.ui.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.home.Chat
import com.xva.kampuschat.helpers.photohelper.PhotoHelper

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {


    var list: List<Chat>? = null
    var inflater: LayoutInflater? = null
    var itemClickListener: ItemClickListener


    constructor(
        context: Context,
        chats: List<Chat>,
        itemClickListener: ItemClickListener
    ) {
        inflater = LayoutInflater.from(context)
        this.list = chats
        this.itemClickListener = itemClickListener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater!!.inflate(R.layout.recyclerview_lists_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = list!![position]
        holder.setData(chat, position, itemClickListener)

    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.Fullname) as TextView
        var department: TextView = itemView.findViewById(R.id.Deparment) as TextView
        var pp: ImageView = itemView.findViewById(R.id.ProfilePhoto) as ImageView
        var settings = itemView.findViewById<ImageView>(R.id.ItemSettings) as ImageView
        var layout = itemView.findViewById<ConstraintLayout>(R.id.item_layout) as ConstraintLayout

        fun setData(chat: Chat, position: Int, clickListener: ItemClickListener) {

            this.name.text = chat.fullname

            if (chat.did_user_banned_me) {

                layout.alpha = 0.5f
                this.department.text = itemView.resources.getString(R.string.text_you_are_banned)

            } else {
                layout.alpha = 1f

                if (chat.last_message.isNotEmpty()) {
                    this.department.text = chat.last_message
                } else {
                    this.department.text = chat.department_name
                }

            }



            if (chat.profile_photo_url != null) {
                pp.setImageBitmap(PhotoHelper.getBitmap(chat.profile_photo_url!!))
            }


            if (chat.notification_signal == 1) {

                settings.setImageDrawable(
                    itemView.resources.getDrawable(
                        R.drawable.ic_notification_signal,
                        itemView.resources.newTheme()
                    )
                )


                settings.setOnClickListener {

                    clickListener.onItemClick("settings", position)
                }


            } else {
                settings.setImageDrawable(
                    itemView.resources.getDrawable(
                        R.drawable.ic_recycler_settings,
                        itemView.resources.newTheme()
                    )
                )
            }

           itemView.setOnClickListener {
               clickListener.onItemClick("view",position)

           }


        }

    }


    interface ItemClickListener {
        fun onItemClick(view: String, position: Int)
    }


}