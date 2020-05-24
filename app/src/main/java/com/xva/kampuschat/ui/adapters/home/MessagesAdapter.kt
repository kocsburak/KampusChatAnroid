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
import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.photohelper.PhotoHelper
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {


    private var list: ArrayList<Message>? = null
    private var inflater: LayoutInflater? = null
    private var user_id: Number
    var itemClickListener: ItemClickListener


    constructor(
        context: Context,
        messages: ArrayList<Message>,
        user_id: Number,
        chat_id: Number,
        itemClickListener: ItemClickListener
    ) {
        inflater = LayoutInflater.from(context)
        this.list = messages
        this.user_id = user_id
        this.itemClickListener = itemClickListener

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesAdapter.MyViewHolder {
        var view = inflater!!.inflate(R.layout.recycler_view_messages, parent, false)
        return MessagesAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessagesAdapter.MyViewHolder, position: Int) {
        val message = list!![position]
        holder.setData(message, user_id, itemClickListener)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var sender = itemView.findViewById<ConstraintLayout>(R.id.Sender)
        var receiver = itemView.findViewById<ConstraintLayout>(R.id.Receiver)


        fun setData(
            message: Message,
            user_id: Number,
            clickListener: ItemClickListener
        ) {


            if (message.sender_user_id != user_id) {
                sender.visibility = View.GONE
                receiver.visibility = View.VISIBLE

                receiver(message, clickListener)

            } else {
                sender(message, clickListener)
            }

        }


        private fun sender(message: Message, clickListener: ItemClickListener) {

            var layout = itemView.findViewById(R.id.TextLayout) as ConstraintLayout
            var text = itemView.findViewById(R.id.SenderMessage) as TextView
            var photo = itemView.findViewById(R.id.Photo) as ImageView
            var date = itemView.findViewById<TextView>(R.id.SenderDate)

            if (message.type == "Text") {

                text.text = message.message

            } else {

                layout.visibility = View.GONE

                photo.visibility = View.VISIBLE

                photo.setImageBitmap(PhotoHelper.getBitmap(message.message))

                photo.setOnClickListener {

                    clickListener.onItemClick(message.message)


                }


            }


            var seen = itemView.findViewById(R.id.SenderSeen) as TextView


            if (message.is_sended) {

                seen.text = itemView.resources.getString(R.string.text_sended)


            }



            if (message.is_seen) {


                seen.text = itemView.resources.getString(R.string.text_seen)

            }

            date.text = getDate(message.created_at)


        }

        private fun receiver(message: Message, clickListener: ItemClickListener) {

            var layout = itemView.findViewById(R.id.ReceiverTextLayout) as ConstraintLayout
            var text = itemView.findViewById<TextView>(R.id.ReceiverMessage)
            var photo = itemView.findViewById(R.id.ReceiverPhoto) as ImageView
            var date = itemView.findViewById<TextView>(R.id.ReceiverDate)

            if (message.type == "Text") {

                text.text = message.message

            } else {

                layout.visibility = View.GONE

                photo.visibility = View.VISIBLE

                photo.setImageBitmap(PhotoHelper.getBitmap(message.message))

                photo.setOnClickListener {

                    clickListener.onItemClick(message.message)


                }


            }

            date.text = getDate(message.created_at)

        }


        private fun getDate(date: String): String {

            var input = SimpleDateFormat("Y-M-D H:m:s", Locale.getDefault())
            var output = SimpleDateFormat("H:m", Locale.getDefault())
            var d = input.parse(date)

            return output.format(d)

        }


    }


    override fun getItemCount(): Int {
        return list!!.size
    }


    interface ItemClickListener {
        fun onItemClick(photo_url: String)
    }


}