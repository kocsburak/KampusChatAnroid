package com.xva.kampuschat.helpers.messagehelper

import android.util.Log
import com.xva.kampuschat.entities.home.Status
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IMessageSeen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeenStatus(var apiService: ApiService, var listener: IMessageSeen):
    Callback<Status> {


    private lateinit var call: Call<Status>

    fun checkIfMessageIsSeen(message_id: Int) {

        call = apiService.isMessageSeen(message_id)
        call.enqueue(this)

    }


    override fun onFailure(call: Call<Status>, t: Throwable) {
        listener.messageSeen(false)
        Log.e("Response","Error")
    }

    override fun onResponse(call: Call<Status>, response: Response<Status>) {



        Log.e("Response Code:",""+response.code())
        Log.e("Res Message:",""+response.message())
        Log.e("Res Message:",""+response.errorBody())
        Log.e("Res Message:",""+response.body())

        if(response.code() == 200){
            var status = response.body()!!


            Log.e("Response",""+status)

            if (status.status) {
                Log.e("Response","true")
                listener.messageSeen(true)
            } else {
                Log.e("Response","true")
                listener.messageSeen(false)
            }
        }

    }


}