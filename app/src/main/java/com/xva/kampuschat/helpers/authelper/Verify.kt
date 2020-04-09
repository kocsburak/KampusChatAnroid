package com.xva.kampuschat.helpers.authelper

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.xva.kampuschat.R
import com.xva.kampuschat.ui.activities.HomeActivity
import com.xva.kampuschat.ui.activities.VerifyActivity
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.verify.IVerify
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Verify(var context: Context, var listener: IVerify) : Callback<Profile> {

    private lateinit var service: ApiService
    private lateinit var call: Call<Profile>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    public fun checkUserVerify() {
        sharedPreferencesHelper =
            SharedPreferencesHelper(context)
        service =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        call = service.getUser()
        call.enqueue(this)
    }


    override fun onFailure(call: Call<Profile>, t: Throwable) {
        Toast.makeText(
            context,
            context.getString(R.string.error_something_wrong),
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {

        if (response.isSuccessful) {
            var user = response.body()!!

            if (user.email_verified_at != null) {
                context.startActivity(Intent(context, HomeActivity::class.java))
                listener.done()

            } else {
                context.startActivity(Intent(context, VerifyActivity::class.java))
                listener.done()
            }

        }


    }


}