package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IMessageSeen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeenStatus(var apiService: ApiService, var listener: IMessageSeen) :
    Callback<String> {


    private lateinit var call: Call<String>


    fun updateMessageSeenValue(message_id: Int) {

        call = apiService.updateMessageSeenValue(message_id)
        call.enqueue(this)


    }


    fun checkIfMessageIsSeen(message_id: Int) {

        call = apiService.isMessageSeen(message_id)
        call.enqueue(this)

    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        listener.messageSeen(null)
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {


        if (response.body() == "true") {
            listener.messageSeen(true)
        } else {
            listener.messageSeen(false)
        }


    }


}