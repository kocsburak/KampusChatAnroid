package com.xva.kampuschat.ui.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.helpers.authelper.Verify
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import com.xva.kampuschat.interfaces.verify.IVerify
import com.xva.kampuschat.ui.activities.HomeActivity


class SplashFragment : Fragment(), IVerify {


    private lateinit var sharedPreferenceshelper: SharedPreferencesHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        sharedPreferenceshelper =
            SharedPreferencesHelper(activity!!)
        checkToken()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    private fun checkToken() {

        var accessToken = sharedPreferenceshelper.getAccessToken()

        if (accessToken.access_token == "") {

            FragmentHelper.changeFragment("Login", activity!!.supportFragmentManager, 1)
            this.onDestroy()

        } else {
            //checkUserVerify() //todo : Burayı geri ekle
            startActivity(
                Intent(
                    activity!!,
                    HomeActivity::class.java
                )
            ) // TODO : Email onaylamayı düzeltince burayı kaldır
        }


    }


    private fun checkUserVerify() {
        var verifyHelper = Verify(activity!!, this)
        verifyHelper.checkUserVerify()
    }


    override fun done() {
        activity!!.finish()
    }


}