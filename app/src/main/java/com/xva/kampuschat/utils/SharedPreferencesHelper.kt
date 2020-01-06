package com.xva.kampuschat.utils

import android.content.Context
import android.content.SharedPreferences
import com.xva.kampuschat.entities.AccessToken

class SharedPreferencesHelper {


    private var mContext: Context
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor


    constructor(context: Context) {

        mContext = context
        pref = mContext.getSharedPreferences("KampusChat", 0)
        editor = pref.edit()

    }


    fun saveEmail(email: String) {
        editor.putString("email", email)
        editor.commit()
    }

    fun getEmail(): String {
        return pref.getString("email","")
    }


    fun saveAccessToken(accessToken: AccessToken) {

        editor.putString("token_type", accessToken.token_type)
        editor.putString("expires_in", accessToken.expires_in)
        editor.putString("access_token", accessToken.access_token)
        editor.putString("refresh_token", accessToken.refresh_token)

        editor.commit()

    }

    fun getAccessToken(): AccessToken {

        return AccessToken(
            pref.getString("token_type", ""),
            pref.getString("expires_in", ""),
            pref.getString("access_token", ""),
            pref.getString("refresh_token", "")
        )

    }


}