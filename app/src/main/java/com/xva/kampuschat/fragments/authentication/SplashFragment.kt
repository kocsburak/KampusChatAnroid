package com.xva.kampuschat.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.activities.HomeActivity
import com.xva.kampuschat.interfaces.IVerify
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import com.xva.kampuschat.utils.VerifyHelper


class SplashFragment : Fragment(), IVerify {


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

            FragmentHelper.changeFragment("Login", activity!!.supportFragmentManager,1)
            this.onDestroy()

        } else {
            //checkUserVerify() todo : Burayı geri ekle
            startActivity(Intent(activity!!,HomeActivity::class.java)) // TODO : Email onaylamayı düzeltince burayı kaldır
        }


    }


    private fun checkUserVerify() {
        var verifyHelper = VerifyHelper(activity!!, this)
        verifyHelper.checkUserVerify()
    }


    override fun done() {
        activity!!.finish()
    }


}