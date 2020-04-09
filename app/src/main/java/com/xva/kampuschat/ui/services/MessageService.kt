package com.xva.kampuschat.ui.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.messagehelper.*
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.IAllMessageArrived
import com.xva.kampuschat.interfaces.message.IMessageSeenUpdated
import com.xva.kampuschat.interfaces.message.IMessageSend
import com.xva.kampuschat.interfaces.message.INewMessagesArrived
import com.xva.kampuschat.interfaces.online.IOnlineStatus
import com.xva.kampuschat.interfaces.typing.ITypingStatus
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MessageService : Service(), IAllMessageArrived, IOnlineStatus,

    IMessageSeenUpdated, ITypingStatus, INewMessagesArrived, IMessageSend {


    private lateinit var apiService: ApiService
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var send_messages_list: ArrayList<Message>
    private lateinit var update_messages_seen_list: ArrayList<Int>

    private var user_online_permission = false
    private var send_message_permission = true
    private var update_message_seen_permission = true
    private var new_messages_permission = false
    private var user_typing_permission = false


    private var other_user_id: Number = -1
    private lateinit var chat_id: Number
    private var update_message_seen_queue_id = 0
    private var send_message_queue_count = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


        sharedPreferencesHelper =
            SharedPreferencesHelper(this)

        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)

        send_messages_list = ArrayList()
        update_messages_seen_list = ArrayList()

        EventBus.getDefault().register(this)

        checkOnlineStatus()

    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    // ONLINE STATUS


    private fun checkOnlineStatus() {

        if (other_user_id != -1) {
            var onlineStatus = OnlineStatus(apiService, this)
            onlineStatus.checkIfUserIsOnline(other_user_id)
            Log.e("checkOnlineStatus", "Ok")
        }

    }

    override fun onlineStatus(status: Boolean?) {

        if (status == null) {
            checkOnlineStatus()
            Log.e("IOnlineStatus", "Error")
        } else {
            EventBus.getDefault().postSticky(EventBusHelper.onlineStatus(status))
            updateOnlinePermission(status)
            delayOnlineStatus()
            Log.e("IOnlineStatus", "OK")
        }

    }

    private fun delayOnlineStatus() {

        val handler = Handler()
        handler.postDelayed(
            {
                checkOnlineStatus()
                Log.e("delayOnlineStatus", "Ok")
            },
            3000
        )
    }


    // GET ALL MESSAGES


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendChatIds) {
        chat_id = data.chat_id

        other_user_id = if (data.owner_id == sharedPreferencesHelper.getEvent().user_id) {
            data.guest_id
        } else {
            data.owner_id
        }

        getAllMessages()
        Log.e("ChatIdEventBus", "OK")
    }


    private fun getAllMessages() {

        var messageHelper = AllMessages(
            apiService,
            sharedPreferencesHelper,
            this
        )
        messageHelper.getAllMessages(chat_id)
        Log.e("getAllMessages", "Ok")

    }

    override fun allMessagesHasArrived(messages: ArrayList<Message>?) {

        if (messages != null) {

            Log.e("IAllMessagesHasArrived", "!=NULL")

            if (messages.size > 0) {
                // update seen

                addSeenQueue(messages, 0)

                if (update_message_seen_permission) {

                    updateMessageSeenValue()
                    updateMessageSeenPermission(false)

                    Log.e("IAllMessagesHasArrived", "Message.size > 0 and Permission = true")
                } else {


                    Log.e("IAllMessagesHasArrived", "Message.size > 0 and Permission = false")
                }

                EventBus.getDefault().post(EventBusHelper.progress(false))

            } else {

                // new message , typing permission

                updateNewMessagePermission(true)
                updateTypingPermission(true)
                checkTypingStatus()
                getNewMessages()
                EventBus.getDefault().post(EventBusHelper.progress(false))
                Log.e("IAllMessagesHasArrived", "Message.size ==0")
            }


        } else {
            Log.e("IAllMessagesHasArrived", "==NULL")
            getAllMessages()
        }

    }


    // UPDATE MESSAGE SEEN VALUE


    /**
     *
     * @param code = 0 -> AllMessages, 1 -> New Messages
     */

    private fun addSeenQueue(messages: ArrayList<Message>?, code: Int) {

        for (item in messages!!) {

            if (!item.is_seen) {
                update_messages_seen_list.add(item.id)
            }

        }

        if (update_message_seen_permission && code != 0) {
            updateMessageSeenValue()
        }

        Log.e("addSeenQueue", "Ok")

    }


    private fun updateMessageSeenValue() {

        if (update_messages_seen_list.size > 0) {
            var messageSeenStatusHelper =
                UpdateSeen(apiService, this)
            messageSeenStatusHelper.updateMessageSeenValue(update_messages_seen_list[update_message_seen_queue_id])
        } else {
            updateNewMessagePermission(true)
            updateTypingPermission(true)
            checkTypingStatus()
            getNewMessages()
        }


    }


    override fun messageSeenUpdated(status: Boolean) {

        if (status) {

            // increase queue id
            update_message_seen_queue_id++
        }

        if (update_message_seen_queue_id < update_messages_seen_list.size) {

            updateMessageSeenValue() // Loop
        } else {

            updateMessageSeenPermission(true)
            // Give Permissions
            updateNewMessagePermission(true)
            updateTypingPermission(true)
            checkTypingStatus()
            getNewMessages()

        }


    }


    // TYPING STATUS

    private fun checkTypingStatus() {

        if (user_online_permission && user_typing_permission) {

            var typingStatus =
                TypingStatus(
                    apiService, this
                )
            typingStatus.checkTypingStatus(chat_id, other_user_id)
            Log.e("checkTypingStatus", "OK")

        } else {
            Log.e("checkTypingStatus", "Denied")
            delayTypingStatus(5000)
        }


    }

    override fun typingStatus(value: Boolean?) {
        // send value to ui

        if (user_online_permission) {
            EventBus.getDefault().post(EventBusHelper.typingStatus(value))
            Log.e("TypingStatus", "Sent")
        }
        delayTypingStatus(2000)
    }


    private fun delayTypingStatus(delay_time: Long) {
        val handler = Handler()
        handler.postDelayed(
            {
                checkTypingStatus()
                Log.e("delayUserTyping", "Ok")
            }, delay_time
        )

    }


    // NEW MESSAGES

    private fun getNewMessages() {

        if (user_online_permission && new_messages_permission) {
            var messageHelper = NewMessages(
                apiService,
                sharedPreferencesHelper, this
            )
            messageHelper.getNewMessages(
                chat_id,
                sharedPreferencesHelper.getEvent().user_id,
                sharedPreferencesHelper.getMessageLastDate()
            )

            Log.e("getNewMessages", "OK")

        } else {

            Log.e("getNewMessages", "Denied")
            delayNewMessages(5000)

        }

    }

    override fun newMessagesHasArrived(messages: ArrayList<Message>?) {

        if (messages != null && messages.size > 0) {
            EventBus.getDefault().post(EventBusHelper.sendMessages(messages))
            updateNewMessagePermission(false)
            updateTypingPermission(false)
            addSeenQueue(messages, 1)

            Log.e("newMessagesHasArrived", "Size > 0")
        } else {

            Log.e("newMessagesHasArrived", "Size == 0")
            delayNewMessages(2000)

        }

    }


    private fun delayNewMessages(delay_time: Long) {

        val handler = Handler()
        handler.postDelayed(
            {
                getNewMessages()
                Log.e("delayNewMessages", "Ok")
            },
            delay_time
        )

    }


    // UPDATE TYPING STATUS


    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.sendTypingStatus) {

        if (data.status != null) {
            updateTypingStatus(data.status)
        }

    }


    private fun updateTypingStatus(value: Boolean?) {
        var helper = UpdateTyping(apiService)
        helper.setUserTypingValue(chat_id, sharedPreferencesHelper.getEvent().user_id, value!!)
    }


    // SEND MESSAGE


    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.sendMessagesToServiceForSending) {

        addMessageQueue(data.message)
        Log.e("EventBusSendMessage", "OK")

    }


    private fun addMessageQueue(message: Message) {

        send_messages_list.add(message)

        if (send_message_permission) {

            updateSendMessagePermission(false)
            sendMessage()

            Log.e("addMessageQueue", "in")
        }

        Log.e("addMessageQueue", "OK")

    }


    private fun sendMessage() {

        var helper = SendMessage(apiService, this)
        helper.sendMessage(send_messages_list[send_message_queue_count])
        Log.e("sendMessage", "OK")


    }

    override fun messageSend(status: Boolean) {

        if (status) {

            send_message_queue_count++

            if (send_message_queue_count < send_messages_list.size) {
                Log.e("messageSend", "Loop")
                sendMessage()
            } else {
                Log.e("messageSend", "Over")
                updateMessageSeenPermission(false)
            }

            Log.e("messageSend", "Status = true")

        } else {
            // sending has failed. Try Again

            Log.e("MessageSend", "Failed")
            sendMessage()

        }


    }


    // PERMISSIONS


    private fun updateOnlinePermission(value: Boolean) {

        user_online_permission = value
        Log.e("OnlinePermission", "$value")
    }


    private fun updateMessageSeenPermission(value: Boolean) {
        update_message_seen_permission = value
        Log.e("UpdateSeenPermission", "$value")

    }

    private fun updateNewMessagePermission(value: Boolean) {

        new_messages_permission = value
        Log.e("NewMessagePermission", "$value")
    }

    private fun updateTypingPermission(value: Boolean) {

        user_typing_permission = value
        Log.e("TypingPermission", "$value")

    }

    private fun updateSendMessagePermission(value: Boolean) {

        send_message_permission = value
        Log.e("SendMessagePermission", "$value")
    }


}