package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.interfaces.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateTyping(var apiService: ApiService) :
    Callback<String> {

    private lateinit var call: Call<String>

    fun setUserTypingValue(chat_id: Number, user_id: Number, value: Boolean) {

        call = apiService.setUserTypingValue(chat_id, user_id, value)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        //
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

        //

    }


}