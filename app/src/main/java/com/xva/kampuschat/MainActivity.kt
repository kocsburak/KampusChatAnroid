package com.xva.kampuschat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.FragmentHelper
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FragmentHelper.changeFragment("Splash",supportFragmentManager)



    }





}
