package com.xva.kampuschat.helpers.chathelper

import android.content.Context
import android.widget.Toast
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.process.IProcessCompleted
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StartChat(var context: Context, var listener: IProcessCompleted) : Callback<String> {

    private lateinit var service: ApiService
    private lateinit var call: Call<String>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    public fun startChat(guest_user_id:Number){
        sharedPreferencesHelper =
            SharedPreferencesHelper(context)

        service =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        call = service.addChat(sharedPreferencesHelper.getEvent().user_id,guest_user_id)
        call.enqueue(this)

    }



    override fun onFailure(call: Call<String>, t: Throwable) {
        Toast.makeText(
            context,
            context.getString(R.string.error_something_wrong),
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if(response.isSuccessful){
           listener.completed()
        }
    }

}