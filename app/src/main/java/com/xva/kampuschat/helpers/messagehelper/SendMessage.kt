package com.xva.kampuschat.helpers.messagehelper

import android.util.Log
import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.photohelper.PhotoHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IMessageSend
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SendMessage(
    var apiService: ApiService,
    var listener: IMessageSend
) : Callback<Message> {


    private lateinit var call: Call<Message>


    fun sendMessage(message: Message) {



        Log.e("URL6",""+message.message)

        if(message.type == "Photo"){
            call = apiService.sendMessage(
                message.chat_id,
                message.sender_user_id,
                message.type,
                message.message
            )
        }else{
            call = apiService.sendMessage(
                message.chat_id,
                message.sender_user_id,
                message.type,
                message.message
            )
        }


        call.enqueue(this)


    }


    override fun onFailure(call: Call<Message>, t: Throwable) {

        Log.e("URL6","Error")
        listener.messageSend(false)
    }

    override fun onResponse(call: Call<Message>, response: Response<Message>) {
        Log.e("URL6","OK")
        listener.messageSend(true)


    }


}