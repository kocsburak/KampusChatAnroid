package com.xva.kampuschat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xva.kampuschat.helpers.uihelper.FragmentHelper

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FragmentHelper.changeFragment("Splash",supportFragmentManager,1)



    }





}
