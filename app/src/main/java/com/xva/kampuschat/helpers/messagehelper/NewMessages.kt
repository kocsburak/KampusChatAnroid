package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.messagehelper.utils.MessageHelper.Companion.setupArrayList
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.INewMessagesArrived
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NewMessages :
    Callback<List<Message>> {

    private lateinit var call: Call<List<Message>>
    private var messages: ArrayList<Message>
    private var apiService: ApiService
    private var sharedPreferencesHelper: SharedPreferencesHelper
    private var listener: INewMessagesArrived


    constructor(
        apiService: ApiService,
        sharedPreferencesHelper: SharedPreferencesHelper,
        listener: INewMessagesArrived
    ) {
        this.apiService = apiService
        this.sharedPreferencesHelper = sharedPreferencesHelper
        this.listener = listener
        messages = ArrayList()


    }


    fun getNewMessages(chat_id: Number, user_id: Number, last_date: String) {
        call = apiService.getNewMessages(chat_id, user_id, last_date)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
        listener.newMessagesHasArrived(null)
    }

    override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
        var messageList = ArrayList<Message>()

        if (response.code() == 200) {

            messageList = setupArrayList(response.body()!!, sharedPreferencesHelper)
        }

        listener.newMessagesHasArrived(messageList)


    }


}