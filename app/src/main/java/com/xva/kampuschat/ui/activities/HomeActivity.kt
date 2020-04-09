package com.xva.kampuschat.ui.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.home.Event
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.eventhelper.SetOnline
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.ui.services.NotificationService
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    Callback<Event> {


    private lateinit var apiService: ApiService
    private lateinit var call: Call<Event>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var dialogHelper: DialogHelper
    private lateinit var onlineHelper: SetOnline


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferencesHelper =
            SharedPreferencesHelper(this)
        onlineHelper = SetOnline(sharedPreferencesHelper)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        dialogHelper = DialogHelper(this)

        dialogHelper.progress()
        updateEvent()


        bottom.setOnNavigationItemSelectedListener(this)

        bottom.selectedItemId = R.id.navigation_shuffle

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        EventBus.getDefault().postSticky(EventBusHelper.updateNotificationPermission(false))
        onlineHelper.setOnline()
        checkIfNotificationServiceisRunning()

    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().postSticky(EventBusHelper.updateNotificationPermission(true))
        EventBus.getDefault().unregister(this)
        onlineHelper.setOffline()
        checkIfNotificationServiceisRunning()
    }


    private fun checkIfNotificationServiceisRunning() {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.runningAppProcesses) {
            if (NotificationService::class.java.name != service.processName) {
                startNotificationServices()
            }
        }

    }


    private fun startNotificationServices() {
        var service = Intent(this, NotificationService::class.java)
        startService(service)
    }



    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.publishFragment) {
        handleBottomNavigation(data.name)
    }


    private fun handleBottomNavigation(fragment: String) {


        if (fragment == "Profile" || fragment == "Shuffle" || fragment == "Bans") {

            bottom.visibility = View.VISIBLE
        } else {
            bottom.visibility = View.GONE
        }


    }


    private fun updateEvent() {

        call = apiService.updateEvent(sharedPreferencesHelper.getEmail())
        call.enqueue(this)

    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0!!.itemId) {

            R.id.navigation_lists -> {
                FragmentHelper.changeFragment("Bans", supportFragmentManager, 1)

                return true
            }
            R.id.navigation_shuffle -> {
                FragmentHelper.changeFragment("Shuffle", supportFragmentManager, 1)
                return true
            }
            R.id.navigation_profile -> {
                FragmentHelper.changeFragment("Profile", supportFragmentManager, 0)
                return true
            }

        }

        return false
    }


    override fun onFailure(call: Call<Event>, t: Throwable) {
        Toast.makeText(this, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<Event>, response: Response<Event>) {
        if (response.isSuccessful) {
            sharedPreferencesHelper.saveEvent(response!!.body()!!)
            dialogHelper.progressDismiss()

            FragmentHelper.changeFragment("Shuffle", supportFragmentManager, 1)

        }
    }


}
