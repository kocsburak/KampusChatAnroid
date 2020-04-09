package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IMessageSeenUpdated
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateSeen(var apiService: ApiService, var listener: IMessageSeenUpdated) : Callback<String> {


    private lateinit var call: Call<String>

    fun updateMessageSeenValue(message_id: Int) {

        call = apiService.updateMessageSeenValue(message_id)
        call.enqueue(this)


    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        listener.messageSeenUpdated(false)
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

        listener.messageSeenUpdated(true)

    }


}