package com.xva.kampuschat.utils

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.xva.kampuschat.R
import com.xva.kampuschat.fragments.*
import org.greenrobot.eventbus.EventBus


class FragmentHelper {


    companion object {

        fun changeFragment(name: String, fragmentManager: FragmentManager) {

            var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            when (name) {

                // Authentication

                "Splash" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, SplashFragment())
                }

                "Login" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, LoginFragment())
                }

                "University" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, UniversityFragment())
                }

                "Department" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, DepartmentFragment())
                }

                "Register" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, RegisterFragment())
                }

                "ForgotPassword" -> {
                    fragmentTransaction.replace(R.id.fragment_place_holder, ForgotPassword())
                }

            }
            EventBus.getDefault().postSticky(EventBusHelper.publishFragment(name))
            fragmentTransaction.commit()


        }

    }


}