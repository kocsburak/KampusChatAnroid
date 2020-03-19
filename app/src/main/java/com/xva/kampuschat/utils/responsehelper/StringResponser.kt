package com.xva.kampuschat.utils.responsehelper

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StringResponser : Callback<String> {

    override fun onFailure(call: Call<String>, t: Throwable) {

    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

    }
}