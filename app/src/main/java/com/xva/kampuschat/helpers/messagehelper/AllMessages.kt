package com.xva.kampuschat.helpers.messagehelper

import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.messagehelper.utils.MessageHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IAllMessageArrived
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMessages : Callback<List<Message>> {


    private lateinit var call: Call<List<Message>>
    private var apiService: ApiService
    private var sharedPreferencesHelper: SharedPreferencesHelper
    private var listener: IAllMessageArrived


    constructor(
        apiService: ApiService,
        sharedPreferencesHelper: SharedPreferencesHelper,
        listener: IAllMessageArrived
    ) {
        this.apiService = apiService
        this.sharedPreferencesHelper = sharedPreferencesHelper
        this.listener = listener


    }


    fun getAllMessages(chat_id: Number) {
        call = apiService.getAllMessages(chat_id)
        call.enqueue(this)
    }



    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
        listener.allMessagesHasArrived(null)
    }

    override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {

        var messageList = ArrayList<Message>()

        if (response.code() == 200) {

            messageList = MessageHelper.setupArrayList(response.body()!!, sharedPreferencesHelper)
        }


        EventBus.getDefault().post(EventBusHelper.messages(messageList))

        listener.allMessagesHasArrived(messageList)



    }


}