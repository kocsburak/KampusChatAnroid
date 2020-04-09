package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.entities.home.Message
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

        call = apiService.sendMessage(
            message.chat_id,
            message.sender_user_id,
            message.type,
            message.message
        )
        call.enqueue(this)


    }


    override fun onFailure(call: Call<Message>, t: Throwable) {

        listener.messageSend(false)
    }

    override fun onResponse(call: Call<Message>, response: Response<Message>) {

        listener.messageSend(true)


    }


}