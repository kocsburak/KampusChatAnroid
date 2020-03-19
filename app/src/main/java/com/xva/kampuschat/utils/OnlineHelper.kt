package com.xva.kampuschat.utils

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IOnlineStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnlineHelper(var sharedPreferencesHelper: SharedPreferencesHelper) : Callback<String> {


    private lateinit var listener: IOnlineStatus
    private var apiService =
        RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
    private lateinit var call: Call<String>

    public fun setOnline() {

        if (sharedPreferencesHelper.getEvent().user_id != -1) {

            call = apiService.updateOnlineStatus(sharedPreferencesHelper.getEvent().user_id, true)
            call.enqueue(this)
        }


    }


    public fun setOffline() {

        if (sharedPreferencesHelper.getEvent().user_id != -1) {

            call = apiService.updateOnlineStatus(sharedPreferencesHelper.getEvent().user_id, false)
            call.enqueue(this)
        }


    }


    public fun checkIfUserIsOnline(user_id: Number, listener: IOnlineStatus) {

        this.listener = listener
        call = apiService.checkIfUserIsOnline(user_id)
        call.enqueue(this)


    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        //
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {


        if (response.code() == 200) {

            if (response.body()!! == "true") {
                listener.setUserOnlineValue(true)
            } else {
                listener.setUserOnlineValue(false)
            }

        }


    }


}