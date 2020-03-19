package com.xva.kampuschat.utils.messagehelper

import com.xva.kampuschat.entities.Message
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IMessageSend
import com.xva.kampuschat.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SendMessageHelper(
    var apiService: ApiService,
    var listener: IMessageSend
) : Callback<String> {


    private lateinit var call: Call<String>


    fun sendMessage(message: Message) {

        call = apiService.sendMessage(
            message.chat_id,
            message.sender_user_id,
            message.type,
            message.message
        )
        call.enqueue(this)


    }


    override fun onFailure(call: Call<String>, t: Throwable) {

        listener.isSended(false)
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

        listener.isSended(true)


    }


}