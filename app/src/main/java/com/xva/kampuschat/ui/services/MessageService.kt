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
import com.xva.kampuschat.helpers.photohelper.PhotoHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.message.*
import com.xva.kampuschat.interfaces.online.IOnlineStatus
import com.xva.kampuschat.interfaces.typing.ITypingStatus
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MessageService : Service(), IAllMessageArrived, IOnlineStatus,

    IMessageSeenUpdated, ITypingStatus, INewMessagesArrived, IMessageSend, IMessageSeen {


    private lateinit var apiService: ApiService
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var send_messages_list: ArrayList<Message>
    private lateinit var update_messages_seen_list: ArrayList<Int>
    private lateinit var check_message_seen_list: ArrayList<Message>

    private var user_online_permission = false
    private var send_message_permission = true
    private var update_message_seen_permission = true
    private var new_messages_permission = false
    private var user_typing_permission = false
    private var check_message_seen_permission = true


    private var other_user_id: Number = -1
    private lateinit var chat_id: Number
    private var update_message_seen_queue_id = 0
    private var send_message_queue_count = 0
    private var check_message_seen_list_count = 0
    private var last_message_id = -1


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
        check_message_seen_list = ArrayList()

        EventBus.getDefault().register(this)

        checkOnlineStatus()
        Log.e("Service","Calisti")

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

        if(sharedPreferencesHelper.getServiceStatus()){
            getAllMessages()
        }else{
            sharedPreferencesHelper.saveServiceStatus(true)
        }


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

                getLastMessageId(messages)
                addSeenQueue(messages, 0)
                checkAllMessageForUnseenMessage(messages)

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


    private fun getLastMessageId(messages: ArrayList<Message>) {

        for (message in messages) {

            if (message.id > last_message_id) {
                last_message_id = message.id
            }


        }


    }


    // UPDATE MESSAGE SEEN VALUE


    /**
     *
     * @param code = 0 -> AllMessages, 1 -> New Messages
     */

    private fun addSeenQueue(messages: ArrayList<Message>?, code: Int) {

        for (item in messages!!) {

            if (!item.is_seen && item.sender_user_id != sharedPreferencesHelper.getEvent().user_id) {
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
            //updateNewMessagePermission(false)
            updateTypingPermission(false)
            addSeenQueue(messages, 1)

            getNewMessages()
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


        if(data.message.type == "Photo"){

            data.message.message = PhotoHelper.url!!
        }

        Log.e("URL4",""+data.message)

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
        last_message_id++
        send_messages_list[send_message_queue_count].id = last_message_id
        helper.sendMessage(send_messages_list[send_message_queue_count])
        Log.e("URL5",""+send_messages_list[send_message_queue_count].message)
        Log.e("sendMessage", "OK")


    }

    override fun messageSend(status: Boolean) {

        if (status) {

            // addCheckSeenQueue(send_messages_list[send_message_queue_count])
            EventBus.getDefault()
                .post(EventBusHelper.isSended(send_messages_list[send_message_queue_count].id))
            addCheckSeenQueue(send_messages_list[send_message_queue_count])
            Log.e("SenMessageId", "" + send_messages_list[send_message_queue_count].id)
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


    private fun checkAllMessageForUnseenMessage(messages: ArrayList<Message>) {

        for (message in messages) {


            if (!message.is_seen) {
                Log.e("Message_Id", "" + message.id)
                check_message_seen_list.add(message)
            }

        }

        if (check_message_seen_list.size > 0) {

            check_message_seen_permission = false
            checkIfMessageSeen()

        }


    }


    private fun addCheckSeenQueue(message: Message) {

        check_message_seen_list.add(message)


        if (check_message_seen_permission) {

            check_message_seen_permission = false
            checkIfMessageSeen()

        }
        Log.e("ListSize", "" + check_message_seen_list.size)


    }


    private fun checkIfMessageSeen() {


        var helper = SeenStatus(apiService, this)
        Log.e("MessageId:", "" + check_message_seen_list[check_message_seen_list_count].id)
        helper.checkIfMessageIsSeen(check_message_seen_list[check_message_seen_list_count].id)


    }


    override fun messageSeen(status: Boolean) {

        Log.e("messageSeen", "oldu")

        if (status) {

            Log.e("messageSeen", "oldu")
            EventBus.getDefault()
                .post(EventBusHelper.updateSeenValue(check_message_seen_list[check_message_seen_list_count].id))
            Log.e("MessageSeenId", "" + check_message_seen_list[check_message_seen_list_count].id)
            check_message_seen_list_count++
            delayCheckMessageSeen()

        } else {

            delayCheckMessageSeen()
        }

    }


    private fun delayCheckMessageSeen() {

        val handler = Handler()
        handler.postDelayed(
            {

                if (check_message_seen_list_count < check_message_seen_list.size) {
                    checkIfMessageSeen()
                } else {
                    check_message_seen_permission = true
                }

                Log.e("delayCheckMessageSeen", "Ok")
            },
            1000
        )

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

    private fun updateCheckMessageSeenPermission(value: Boolean) {

        check_message_seen_permission = value

    }


}