package com.xva.kampuschat.helpers.eventhelper

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.home.Status
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.online.IOnlineProcess
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetOnline(var sharedPreferencesHelper: SharedPreferencesHelper) : Callback<Status> {


    private lateinit var listener: IOnlineProcess
    private var apiService =
        RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
    private lateinit var call: Call<Status>

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


    public fun checkIfUserIsOnline(user_id: Number, listener: IOnlineProcess) {

        this.listener = listener
        call = apiService.checkIfUserIsOnline(user_id)
        call.enqueue(this)


    }


    override fun onFailure(call: Call<Status>, t: Throwable) {
        //
    }

    override fun onResponse(call: Call<Status>, response: Response<Status>) {


        if (response.code() == 200) {

            var status = response.body()!!

            if (status.status) {
                listener.setUserOnlineValue(true)
            } else {
                listener.setUserOnlineValue(false)
            }

        }


    }


}