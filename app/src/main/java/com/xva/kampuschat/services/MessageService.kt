package com.xva.kampuschat.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Chat
import com.xva.kampuschat.entities.Message
import com.xva.kampuschat.interfaces.*
import com.xva.kampuschat.utils.EventBusHelper
import com.xva.kampuschat.utils.OnlineHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import com.xva.kampuschat.utils.messagehelper.GetMessageHelper
import com.xva.kampuschat.utils.messagehelper.MessageSeenStatusHelper
import com.xva.kampuschat.utils.messagehelper.SendMessageHelper
import com.xva.kampuschat.utils.messagehelper.UserTypingStatusHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MessageService : Service(), IProcessCompleted, IOnlineStatus, ITypingStatus,
    IMessageSeenUpdated, IMessageSend, IMessageSeen {


    private lateinit var apiService: ApiService
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var chat: Chat
    private lateinit var send_messages_list: ArrayList<Message>
    private lateinit var update_messages_seen_list: ArrayList<Int>


    private var is_user_online = false

    private var send_message_permission = true
    private var update_message_seen_permission = true
    private var other_user_id = -1;

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()

        EventBus.getDefault().register(this)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)

        send_messages_list = ArrayList()
        update_messages_seen_list = ArrayList()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendChatInformations) {
        chat = data.chat

        if (chat.owner_user_id == sharedPreferencesHelper.getEvent().user_id) {
            other_user_id = chat.guest_user_id
        } else {
            other_user_id = chat.owner_user_id
        }
        getAllMessages()

    }

    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendMessagesToService) {

        if (data.processCode == 1) {

            for (item in data.messages!!) {
                update_messages_seen_list.add(item.id)

            }

            if (update_message_seen_permission) {
                updateMessageSeenValue()
            }


        } else {


            for (item in data.messages!!) {

                send_messages_list.add(item)

            }

            if (send_message_permission) {

                sendMessage()

            }


        }


    }


    private fun getAllMessages() {

        var messageHelper = GetMessageHelper(
            apiService,
            sharedPreferencesHelper,
            this
        )
        messageHelper.getAllMessages(chat.id)
        Log.e("getAllMessages", "Ok")

    }

    // Start Loops
    override fun completed() {
        checkIfUserIsOnline()
        checkIfUserIsTyping()
        getNewMessages()
    }


    private fun checkIfUserIsOnline() {

        var onlineHelper = OnlineHelper(sharedPreferencesHelper)
        onlineHelper.checkIfUserIsOnline(other_user_id, this)

    }

    override fun setUserOnlineValue(value: Boolean) {
        is_user_online = value
        delayUserOnline()
    }

    private fun delayUserOnline() {

        val handler = Handler()
        handler.postDelayed(
            {
                delayUserOnline()
                Log.e("delayUserOnline", "Ok")
            },
            3000
        )
    }


    private fun checkIfUserIsTyping() {

        if (is_user_online) {

            var userTypingStatusHelper =
                UserTypingStatusHelper(
                    sharedPreferencesHelper
                )
            userTypingStatusHelper.checkIfUserIsTyping(chat.id, other_user_id, this)

        } else {

            delayUserTyping()
        }


    }

    override fun setUserTypingValue(value: Boolean) {
        // send value to ui
        delayUserTyping()

    }


    private fun delayUserTyping() {
        val handler = Handler()
        handler.postDelayed(
            {
                checkIfUserIsTyping()
                Log.e("delayUserTyping", "Ok")
            },
            2000
        )

    }


    private fun getNewMessages() {

        if(is_user_online){
            var messageHelper = GetMessageHelper(
                apiService,
                sharedPreferencesHelper,
                null
            )
            messageHelper.getNewMessages(
                chat.id,
                sharedPreferencesHelper.getEvent().user_id,
                sharedPreferencesHelper.getMessageLastDate()
            )
        }
        newMessagesDelay()
    }


    private fun newMessagesDelay() {

        val handler = Handler()
        handler.postDelayed(
            {
                getNewMessages()
                Log.e("delayNewMessages", "Ok")
            },
            5000
        )

    }


    private fun updateMessageSeenValue() {

        var messageSeenStatusHelper =
            MessageSeenStatusHelper(apiService, (this as IMessageSeenUpdated), 1)
        messageSeenStatusHelper.updateMessageSeenValue(update_messages_seen_list[0])

    }


    override fun isUpdated(status: Boolean) {

        if (status) {
            update_messages_seen_list.removeAt(0)

        }

        // else tekrar gönder ve true olsada liste doluysa devam et
        if (update_messages_seen_list.size > 0) {
            updateMessageSeenValue()
        }


    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.isTyping) {
       setTypingValue(data.statue)
    }


    private fun setTypingValue(value: Boolean) {

        var userTypingStatusHelper = UserTypingStatusHelper(sharedPreferencesHelper)
        userTypingStatusHelper.setUserTypingValue(
            chat.id,
            sharedPreferencesHelper.getEvent().user_id,
            value
        )

    }


    var send_message_id = 0

    private fun sendMessage() {

        var sendMessageHelper = SendMessageHelper(apiService, this)
        sendMessageHelper.sendMessage(send_messages_list[send_message_id])

    }

    override fun isSended(status: Boolean) {

        if (status) {

            send_message_id++

            if (send_message_id < send_messages_list.size) {
                // Gönderilmesi Gereken Mesaj Varsa
                sendMessage()
            }

            if (!is_checkIfUserSeenMessage_funtion_working) {
                checkIfUserSeenMessage()
            }


        } else {
            // tekrar göndermeyi dene
            sendMessage()
        }

    }

    var is_checkIfUserSeenMessage_funtion_working = false
    var checked_message_id = 0
    private fun checkIfUserSeenMessage() {

        if (checked_message_id < send_message_id) {
            if (is_user_online) {

                var messageSeenStatusHelper =
                    MessageSeenStatusHelper(apiService, (this as IMessageSeen), 2)
                messageSeenStatusHelper.checkIfMessageIsSeen(send_messages_list[checked_message_id].id)

            } else {
                delayUserSeenMessage()
            }


        } else {
            // işlem tamamlanmış
            is_checkIfUserSeenMessage_funtion_working = false
        }

    }


    private fun delayUserSeenMessage() {
        // Kullanıcının online olmasını bekle

        val handler = Handler()
        handler.postDelayed(
            {
                checkIfUserSeenMessage()
                Log.e("delayUserSeenMessages", "Ok")
            },
            3000
        )


    }

    override fun isSeen(status: Boolean) {

        if (status) {
            checked_message_id++

        }

        // else yada normal durum
        checkIfUserSeenMessage()

    }

}