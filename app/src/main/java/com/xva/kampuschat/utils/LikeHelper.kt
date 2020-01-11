package com.xva.kampuschat.utils

import android.content.Context
import android.widget.Toast
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.ILike
import com.xva.kampuschat.interfaces.IVerify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LikeHelper(var context: Context, var listener: ILike)  : Callback<String> {



    private lateinit var service: ApiService
    private lateinit var call: Call<String>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    public fun likeUser(liked_user_id : Number){


        sharedPreferencesHelper = SharedPreferencesHelper(context)
        service =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        call = service.likeUser(sharedPreferencesHelper.getEvent().user_id,liked_user_id)
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

            if(response.code() == 204){

                listener.liked(0)

            }

            if(response.code() == 200){

                listener.liked(1)

            }


        }
    }




}