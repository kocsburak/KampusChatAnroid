package com.xva.kampuschat.utils.messagehelper

import com.xva.kampuschat.entities.Message
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IProcessCompleted
import com.xva.kampuschat.utils.EventBusHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

open class GetMessageHelper :
    Callback<List<Message>> {

    lateinit var call: Call<List<Message>>
    var messages: ArrayList<Message>
    open var apiService: ApiService
    open var sharedPreferencesHelper: SharedPreferencesHelper
    open var listener:IProcessCompleted? = null


    constructor(
        apiService: ApiService,
        sharedPreferencesHelper: SharedPreferencesHelper,
        listener: IProcessCompleted?
    ) {
        this.apiService = apiService
        this.sharedPreferencesHelper = sharedPreferencesHelper
        this.listener = listener as IProcessCompleted
        messages = ArrayList()

    }


    fun getAllMessages(chat_id: Number) {
        call = apiService.getAllMessages(chat_id)
        call.enqueue(this)
    }

    fun getNewMessages(chat_id: Number, user_id: Number, last_date: String) {
        call = apiService.getNewMessages(chat_id, user_id, last_date)
        call.enqueue(this)
    }



    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
        EventBus.getDefault().postSticky(EventBusHelper.sendMessages(null))
    }

    override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
        var messageList = ArrayList<Message>()

        if (response.code() == 200) {

            setupArrayList(response.body()!!, messageList)
        }

        if (listener != null) {
            listener!!.completed()
        }


        EventBus.getDefault().postSticky(
            EventBusHelper.sendMessages(
                messageList
            )
        )
    }


     fun setupArrayList(
        list: List<Message>,
        messageList: ArrayList<Message>
    ) {

        for (item in list) {

            compareDates(item.created_at)
            messageList.add(item)
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


}