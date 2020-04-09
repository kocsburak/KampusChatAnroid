package com.xva.kampuschat.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.ui.activities.HomeActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class NotificationService : Service(), Callback<List<Message>> {


    private lateinit var apiService: ApiService
    private lateinit var call: Call<List<Message>>
    private lateinit var messages: ArrayList<Message>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private var permission = true

    private var messages_permission = true

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        Log.e("onCreate", "OK")


        sharedPreferencesHelper =
            SharedPreferencesHelper(this)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        messages = ArrayList()


        checkNewMessages()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.updateNotificationPermission) {
        permission = data.permission
        Log.e("permission", "" + permission)
    }


    // FROM Message Service
    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.notificationService) {
        messages_permission = data.status
        Log.e("messages_permission", "" + permission)
    }



    private fun checkNewMessages() {

        if (messages_permission){
            var time = "2020-01-01 00:00:00"

            if (permission) {
                time = sharedPreferencesHelper.getMessageLastDate()
            }

            call = apiService.checkNewMessages(sharedPreferencesHelper.getEvent().user_id, time)
            call.enqueue(this)
        }

    }


    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
        delay()
        Log.e("onFailure", "404")
    }

    override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
        if (response.code() == 200) {


            if(messages_permission){

                var list = response.body()!!
                setupArrayList(list)
                Log.e("onResponse", "200")

                if (permission) {
                    createNotificationChannel()
                    sendNotification()
                }
                // Log.e("Date",last_date)
            }

        }

        delay()
    }

    private fun setupArrayList(list: List<Message>) {

        if(messages_permission){
            for (item in list) {
                messages.add(item)

                compareDates(item.created_at)

            }

            EventBus.getDefault().post(EventBusHelper.sendMessages(messages))
        }

    }


    private fun compareDates(date: String) {

        val dateFormat = SimpleDateFormat("y-M-d H:m:s")
        var date1 = dateFormat.parse(date)
        var date2 = dateFormat.parse(sharedPreferencesHelper.getMessageLastDate())

        if (date1.after(date2)) {
            sharedPreferencesHelper.saveMessageLastDate(date)
        }


    }

    private fun sendNotification() {


        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.ic_whatshot_black_24dp)
            .setContentTitle(this.getString(R.string.text_notification_title))
            .setContentText(this.getString(R.string.text_notification))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }


        Log.e("notifi", "sended")

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifi"
            val descriptionText = "Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun delay() {

        val handler = Handler()
        handler.postDelayed(
            {
                checkNewMessages()
                Log.e("delay", "Ok")
            },
            5000
        )

    }


}