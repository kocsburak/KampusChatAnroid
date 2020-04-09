package com.xva.kampuschat.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.photohelper.Picasso
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import com.xva.kampuschat.ui.adapters.home.MessagesAdapter
import com.xva.kampuschat.ui.services.MessageService
import kotlinx.android.synthetic.main.fragment_message.view.*
import kotlinx.android.synthetic.main.header_message.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageFragment : Fragment(), MessagesAdapter.ItemClickListener, View.OnClickListener,
    TextWatcher {


    private lateinit var mView: View

    private lateinit var intent: Intent

    private lateinit var messages: ArrayList<Message>

    private lateinit var adapter: MessagesAdapter


    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var dialogHelper: DialogHelper


    private var chat_id = -1
    private var typing_status = false

    override fun onStart() {
        super.onStart()

        startMessageService()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        stopMessageService()
        EventBus.getDefault().unregister(this)
    }


    private fun startMessageService() {

        intent = Intent(activity!!, MessageService::class.java)
        activity!!.startService(intent)
        turnOffNotificationService()

    }

    private fun stopMessageService() {
        activity!!.stopService(intent)
        turnOnNotificationService()
    }

    private fun turnOffNotificationService() {
        EventBus.getDefault().postSticky(EventBusHelper.notificationService(false))
    }

    private fun turnOnNotificationService() {
        EventBus.getDefault().postSticky(EventBusHelper.notificationService(true))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_message, container, false)

        mView.Back.setOnClickListener(this)

        mView.Ban.setOnClickListener(this)

        mView.Send.setOnClickListener(this)

        mView.TextMessage.addTextChangedListener(this)

        messages = ArrayList()

        sharedPreferencesHelper = SharedPreferencesHelper(activity!!)

        dialogHelper = DialogHelper(activity!!)


        showDialog()
        setupAdapter()

        return mView
    }


    // Chat Informations From ChatList Fragment

    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendChatInformations) {

        chat_id = data.chat.id

        mView.Fullname.text = data.chat.fullname

        mView.Status.text = data.chat.department_name

        if (data.chat.profile_photo_url != null && data.chat.profile_photo_url != "") {

            Picasso.loadPhoto(data.chat.profile_photo_url!!, mView.ProfilePhoto)
        }


        EventBus.getDefault().postSticky(
            EventBusHelper.sendChatIds(
                data.chat.id,
                data.chat.owner_user_id,
                data.chat.guest_user_id
            )
        )


    }


    // Online Status From Message Service

    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.onlineStatus) {

        if (data.status) {

            mView.Status.text = getString(R.string.text_online)

        } else {

            mView.Status.text = getString(R.string.text_offline)
        }


    }


    private fun showDialog() {
        dialogHelper.progress()
    }


    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.progress) {

        dismissDialog()

    }

    private fun dismissDialog() {
        dialogHelper.progressDismiss()
    }


    private fun setupAdapter() {

        adapter =
            MessagesAdapter(
                activity!!,
                messages,
                sharedPreferencesHelper.getEvent().user_id,
                chat_id,
                this
            )

        var linearLayoutManager = LinearLayoutManager(activity!!)

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        mView.Messages.layoutManager = linearLayoutManager

        adapter.notifyDataSetChanged()

        mView.Messages.adapter = adapter

    }


    // IF PHOTO IS CLICKED THEN SHOW PHOTO THROUGH DIALOG
    override fun onItemClick(photo_url: String) {
        //dialogHelper.showPhoto(photo_url)

    }

    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.messages) {


        if (data.messages != null && data.messages!!.size > 0) {


            for (item in data.messages!!) {
                messages.add(item)

            }

            adapter.notifyDataSetChanged()
            mView.Messages.scrollToPosition(messages.size-1)
        }
        Log.e("Messages", "Ok")
    }

    // Typing Status

    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.typingStatus) {


        if (data.status!!) {
            mView.Status.text = getString(R.string.text_typing)
        } else {

            mView.Status.text = getString(R.string.text_online)
        }


        Log.e("Typing Status", "Ok")

    }


    @Subscribe
    internal fun onDataEvent(data: EventBusHelper.sendMessages) {

        for (message in data.messages!!) {

            messages.add(message)

        }

        adapter.notifyDataSetChanged()
        mView.Messages.scrollToPosition(messages.size-1)
    }


    // Typing


    override fun afterTextChanged(s: Editable?) {
        //
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (count > 0 && !typing_status) {

            EventBus.getDefault().post(EventBusHelper.sendTypingStatus(true))
            typing_status = true
        } else if (count == 0 && typing_status) {

            EventBus.getDefault().post(EventBusHelper.sendTypingStatus(false))
            typing_status = false

        }


    }


    // Send Message


    private fun sendMessage() {


        if (mView.TextMessage.text.isNotEmpty()) {

            var message = Message()

            message.chat_id = chat_id
            message.message = mView.TextMessage.text.toString()
            message.sender_user_id = sharedPreferencesHelper.getEvent().user_id
            message.type = "Text"
            message.is_seen = false
            message.created_at = getDate()
            message.updated_at = getDate()

            EventBus.getDefault().post(EventBusHelper.sendMessagesToServiceForSending(message))

            messages.add(message)
            adapter.notifyDataSetChanged()
            mView.Messages.scrollToPosition(messages.size-1)


        }


    }


    private fun getDate(): String {

        var format = SimpleDateFormat("Y-M-D H:m:s", Locale.getDefault())
        return format.format(Date())

    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.Ban -> {

            }

            R.id.Back -> {

                stopMessageService()
                turnOnNotificationService()
                this.onStop()
                activity!!.supportFragmentManager.fragments.remove(this)
                FragmentHelper.changeFragment("ChatList", activity!!.supportFragmentManager, 0)
            }

            R.id.Send -> {
                sendMessage()

                mView.TextMessage.text = null
            }


        }
    }


}