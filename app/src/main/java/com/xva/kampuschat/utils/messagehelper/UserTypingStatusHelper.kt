package com.xva.kampuschat.utils.messagehelper

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.ITypingStatus
import com.xva.kampuschat.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserTypingStatusHelper(var sharedPreferencesHelper: SharedPreferencesHelper) :
    Callback<String> {

    private lateinit var listener: ITypingStatus
    private var apiService =
        RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
    private lateinit var call: Call<String>



    public fun checkIfUserIsTyping(chat_id:Number,user_id: Number, listener: ITypingStatus) {

        this.listener = listener
        call = apiService.checkIfUserTyping(chat_id,user_id)
        call.enqueue(this)


    }

    fun setUserTypingValue(chat_id: Number,user_id: Number,value:Boolean){

        call = apiService.setUserTypingValue(chat_id, user_id, value)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        //
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {


        if (response.code() == 200) {

            if (response.body()!! == "true") {
                listener.setUserTypingValue(true)
            } else {
                listener.setUserTypingValue(false)
            }

        }


    }



}