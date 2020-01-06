package com.xva.kampuschat.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.xva.kampuschat.R
import com.xva.kampuschat.activities.HomeActivity
import com.xva.kampuschat.activities.VerifyActivity
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.User
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IVerify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyHelper(var context: Context,var listener : IVerify) : Callback<User> {

    private lateinit var service: ApiService
    private lateinit var call: Call<User>

    public fun checkUserVerify() {
        service = RetrofitBuilder.createService(ApiService::class.java)
        call = service.getUser()
        call.enqueue(this)
    }


    override fun onFailure(call: Call<User>, t: Throwable) {
        Toast.makeText(context, context.getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<User>, response: Response<User>) {

        if (response.isSuccessful) {
            var user = response.body()!!

            if(user.is_verified){
                context.startActivity(Intent(context, HomeActivity::class.java))

            }else{
                context.startActivity(Intent(context, VerifyActivity::class.java))
            }

        }


    }



}