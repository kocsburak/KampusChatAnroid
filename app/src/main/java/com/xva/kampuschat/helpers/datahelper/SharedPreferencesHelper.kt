package com.xva.kampuschat.helpers.datahelper

import android.content.Context
import android.content.SharedPreferences
import com.xva.kampuschat.entities.auth.AccessToken
import com.xva.kampuschat.entities.home.Event

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
        return pref.getString("email", "")
    }


    fun saveAccessToken(accessToken: AccessToken?) {

        editor.putString("token_type", accessToken!!.token_type)
        editor.putString("expires_in", accessToken!!.expires_in)
        editor.putString("access_token", accessToken!!.access_token)
        editor.putString("refresh_token", accessToken!!.refresh_token)

        editor.commit()

    }

    fun deleteAccessToken() {

        editor.putString("token_type", null)
        editor.putString("expires_in", null)
        editor.putString("access_token", null)
        editor.putString("refresh_token", null)

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

    fun saveEvent(event: Event?) {

        editor.putInt("event_id", event!!.id)
        editor.putInt("event_user_id", event!!.user_id)
        editor.putInt("event_group", event!!.group)
        editor.putBoolean("event_is_online", event!!.is_online)
        editor.putString("event_last_seen_at", event!!.last_seen_at)
        editor.commit()
    }

    fun getEvent(): Event {

        var event = Event()
        event.id = pref.getInt("event_id", -1)
        event.user_id = pref.getInt("event_user_id", -1)
        event.group = pref.getInt("event_group", -1)
        event.is_online = pref.getBoolean("event_is_online", false)
        event.last_seen_at = pref.getString("event_last_seen_at", "")
        event.created_at = ""
        event.updated_at = ""


        return event

    }

    fun saveMessageLastDate(date: String) {

        editor.putString("messageLastDate", date)
        editor.commit()

    }

    fun getMessageLastDate(): String {
        return pref.getString("messageLastDate", "2020-01-12 00:00:00");
    }


    fun saveServiceStatus(status:Boolean) {


        editor.putBoolean("status",status)
        editor.commit()

    }


    fun getServiceStatus(): Boolean {

        return pref.getBoolean("status",true)
    }


}