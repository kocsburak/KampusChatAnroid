package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.home.Status
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.typing.ITypingStatus
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypingStatus(var apiService: ApiService,var listener: ITypingStatus) :
    Callback<Status> {

    private lateinit var call: Call<Status>



    public fun checkTypingStatus(chat_id:Number,user_id: Number) {

        call = apiService.checkIfUserTyping(chat_id,user_id)
        call.enqueue(this)


    }


    override fun onFailure(call: Call<Status>, t: Throwable) {
        listener.typingStatus(null)
    }

    override fun onResponse(call: Call<Status>, response: Response<Status>) {


        if (response.code() == 200) {

            var status = response.body()!!

           listener.typingStatus(status.status!!)

        }


    }



}