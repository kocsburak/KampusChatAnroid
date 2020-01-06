package com.xva.kampuschat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xva.kampuschat.R
import com.xva.kampuschat.utils.EventBusHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.publishFragment) {
        //changeHeader(data.name)
    }





}
