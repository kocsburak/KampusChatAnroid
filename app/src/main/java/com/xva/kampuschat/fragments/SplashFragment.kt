package com.xva.kampuschat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.activities.HomeActivity
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper


class SplashFragment : Fragment() {


    private lateinit var sharedPreferenceshelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        sharedPreferenceshelper = SharedPreferencesHelper(activity!!)
        checkToken()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    private fun checkToken() {

        var accessToken = sharedPreferenceshelper.getAccessToken()

        if (accessToken.access_token == "") {

            FragmentHelper.changeFragment("Login", activity!!.supportFragmentManager)
            this.onDestroy()

        } else {
            startActivity(Intent(activity!!, HomeActivity::class.java))
            activity!!.finish()
        }


    }


}