package com.xva.kampuschat.helpers.messagehelper


import android.util.Log
import com.xva.kampuschat.entities.home.Status
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.online.IOnlineStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnlineStatus(var apiService: ApiService, var listener: IOnlineStatus) : Callback<Status> {

    private lateinit var call: Call<Status>

    fun checkIfUserIsOnline(user_id: Number) {

        call = apiService.checkIfUserIsOnline(user_id)
        call.enqueue(this)

    }


    override fun onResponse(call: Call<Status>, response: Response<Status>) {

       var status = response.body()!!

        if (status.status) {
            listener.onlineStatus(true)
        } else {
            listener.onlineStatus(false)
        }


    }

    override fun onFailure(call: Call<Status>, t: Throwable) {

        listener.onlineStatus(null)
    }
}