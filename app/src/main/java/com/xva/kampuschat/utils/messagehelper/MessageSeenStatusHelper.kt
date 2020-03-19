package com.xva.kampuschat.utils.messagehelper

import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IMessageSeen
import com.xva.kampuschat.interfaces.IMessageSeenUpdated
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageSeenStatusHelper(var apiService: ApiService, var listener: Any, var processCode: Int) :
    Callback<String> {


    lateinit var call: Call<String>


    fun updateMessageSeenValue(message_id: Int) {

        call = apiService.updateMessageSeenValue(message_id)
        call.enqueue(this)


    }


    fun checkIfMessageIsSeen(message_id: Int) {

        call = apiService.isMessageSeen(message_id)
        call.enqueue(this)

    }


    override fun onFailure(call: Call<String>, t: Throwable) {

        if (processCode == 1) {

            // Update Message Seen

            (listener as IMessageSeenUpdated).isUpdated(false)

        } else {


            (listener as IMessageSeen).isSeen(false)

        }


    }

    override fun onResponse(call: Call<String>, response: Response<String>) {


        if (response.code() == 204) {

            (listener as IMessageSeenUpdated).isUpdated(true)

        }

        if (response.code() == 200) {

            (listener as IMessageSeen).isSeen(true)

        }


    }


}