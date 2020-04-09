package com.xva.kampuschat.helpers.uihelper

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.xva.kampuschat.R
import com.xva.kampuschat.ui.fragments.authentication.*
import com.xva.kampuschat.ui.fragments.home.*
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import org.greenrobot.eventbus.EventBus


class FragmentHelper {


    companion object {

        fun changeFragment(name: String, fragmentManager: FragmentManager,code:Int) {

            var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            when (name) {

                // Authentication

                "Splash" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        SplashFragment()
                    )
                }

                "Login" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        LoginFragment()
                    )
                }

                "University" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        UniversityFragment()
                    )
                }

                "Department" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        DepartmentFragment()
                    )
                }

                "Register" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        RegisterFragment()
                    )
                }

                "ForgotPassword" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,
                        ForgotPassword()
                    )
                }


                "Profile" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, ProfileFragment(code))
                }


                "Bans" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, BansFragment())
                }

                "Shuffle" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, ShuffleFragment())
                }

                "Settings" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, SettingsList())
                }

                "EditProfile" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, EditProfile())
                }

                "ChatList" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, ChatList())
                }

                "Message" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder,MessageFragment())
                }

            }
            EventBus.getDefault().postSticky(EventBusHelper.publishFragment(name))
            fragmentTransaction.commit()


        }

    }


}