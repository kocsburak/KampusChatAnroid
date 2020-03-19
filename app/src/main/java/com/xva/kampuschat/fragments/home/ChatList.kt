package com.xva.kampuschat.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.adapters.ChatListAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Chat
import com.xva.kampuschat.entities.Message
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IProcessCompleted
import com.xva.kampuschat.interfaces.IProcessDialog
import com.xva.kampuschat.utils.*
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_back.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class ChatList : Fragment(), Callback<List<Chat>>, ChatListAdapter.ItemClickListener,
    IProcessDialog, IProcessCompleted {


    private lateinit var mView: View
    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<List<Chat>>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var chats: ArrayList<Chat>
    private lateinit var adapter: ChatListAdapter

    private var listPosition = 0
    private lateinit var processList: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferencesHelper = SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)


        mView.BackButton.setOnClickListener {

            FragmentHelper.changeFragment("Shuffle", activity!!.supportFragmentManager, 1)

        }

        chats = ArrayList()
        getChats()
        return mView
    }


    private fun getChats() {

        dialogHelper.progress()
        call = apiService.getChats(sharedPreferencesHelper.getEvent().user_id)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
        dialogHelper.progressDismiss()
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
        if (response.isSuccessful) {

            if (response.code() == 200) {


                var list = response.body()!!

                setupArrayList(list)
                setupAdapter()
                setupProcessList()
                startEventBus()

            }

            if (response.code() == 204) {
                Toast.makeText(
                    activity!!,
                    getString(R.string.error_nobody_is_found),
                    Toast.LENGTH_LONG
                ).show()
            }


        }

        dialogHelper.progressDismiss()
    }


    private fun setupArrayList(list: List<Chat>) {

        for (item in list) {

            if (!(item.did_i_banned_user)) {
                chats.add(item)
                Log.e("User", item.fullname)
            }

        }
    }

    private fun setupAdapter() {

        adapter = ChatListAdapter(activity!!, chats, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mView.Lists.layoutManager = linearLayoutManager
        adapter.notifyDataSetChanged()
        mView.Lists.adapter = adapter


    }

    private fun setupProcessList() {

        processList = Array(1) { "" }
        processList[0] = (getString(R.string.text_ban))

    }


    override fun onItemClick(view: View, position: Int) {
        listPosition = position

        if (chats[listPosition].notification_signal == 1) {
            EventBus.getDefault()
                .postSticky(EventBusHelper.sendChatInformations(chats[listPosition]))
            FragmentHelper.changeFragment("Message",activity!!.supportFragmentManager,0)
        } else {
            dialogHelper.process(processList, this, getString(R.string.text_choose_process))
        }

    }

    override fun onItemClicked(position: Int) {
        banUser()
    }

    private fun banUser() {
        dialogHelper.progress()
        var banHelper = BanHelper(activity!!, this)

        if (sharedPreferencesHelper.getEvent().user_id == chats[listPosition].owner_user_id) {
            banHelper.ban(chats[listPosition].guest_user_id)
        } else {
            banHelper.ban(chats[listPosition].owner_user_id)
        }


    }

    // user is banned
    override fun completed() {
        chats.removeAt(listPosition)
        dialogHelper.progressDismiss()
    }

    private fun startEventBus() {
        EventBus.getDefault().register(this)

    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendMessages) {
        if (data.messages!!.size > 0) {
            setNewMessages(data.messages!!)
        }

    }

    private fun setNewMessages(messages: ArrayList<Message>) {


        for (message in messages) {

            for (chat in chats) {

                if (message.chat_id == chat.id) {
                    chat.notification_signal = 1
                    chat.message_update_time = message.created_at
                    if (message.type == "Photo") {
                        chat.last_message = getString(R.string.text_sended_a_photo)
                    } else {
                        chat.last_message = message.message
                    }

                }

            }


        }

        sortList()
        adapter.notifyDataSetChanged()


    }


    private fun sortList() {
        chats.sortWith(Comparator { lhs, rhs ->

            if (lhs.message_update_time > rhs.message_update_time) -1 else if (lhs.message_update_time < rhs.message_update_time) 1 else 0
        })


    }


}