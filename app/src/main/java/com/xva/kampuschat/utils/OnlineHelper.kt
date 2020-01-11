package com.xva.kampuschat.utils

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnlineHelper(var sharedPreferencesHelper: SharedPreferencesHelper) : Callback<String> {


    private var apiService = RetrofitBuilder.createServiceWithAuth(ApiService::class.java,sharedPreferencesHelper)
    private lateinit var call:Call<String>

    public fun setOnline(){

        if(sharedPreferencesHelper.getEvent().user_id != -1){

            call = apiService.setOnline(sharedPreferencesHelper.getEvent().user_id)
            call.enqueue(this)
        }


    }


    public fun setOffline(){

        if(sharedPreferencesHelper.getEvent().user_id != -1){

            call = apiService.setOffline(sharedPreferencesHelper.getEvent().user_id)
            call.enqueue(this)
        }


    }



    override fun onFailure(call: Call<String>, t: Throwable) {
        //
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        //
    }




}