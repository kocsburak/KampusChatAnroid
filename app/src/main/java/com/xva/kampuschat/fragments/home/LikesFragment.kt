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
import com.xva.kampuschat.adapters.ListsAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Profile
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IProcessCompleted
import com.xva.kampuschat.interfaces.IProcessDialog
import com.xva.kampuschat.utils.*
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_likes_bans.view.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikesFragment : Fragment(), Callback<List<Profile>>, ListsAdapter.ItemClickListener,
    IProcessDialog, IProcessCompleted {


    private lateinit var mView: View
    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<List<Profile>>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var profiles: List<Profile>
    private lateinit var banHelper: BanHelper
    private lateinit var adapter: ListsAdapter

    private var listPosition = 0

    private lateinit var processList: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_lists, container, false)
        sharedPreferencesHelper = SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        profiles = ArrayList()
        setClicks()
        getLikes()
        return mView
    }

    private fun setClicks() {

        mView.BansListButton.setOnClickListener {

            FragmentHelper.changeFragment("Bans", activity!!.supportFragmentManager,1)
        }

    }


    private fun getLikes() {
        dialogHelper.progress()
        call = apiService.getLikes(sharedPreferencesHelper.getEvent().user_id)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<List<Profile>>, t: Throwable) {
        dialogHelper.progressDismiss()
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()


        Log.e("Error", t.printStackTrace().toString())
    }

    override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
        if (response.isSuccessful) {

            if (response.code() == 200) {

                profiles = response.body()!!


                setupAdapter()
                adapter.notifyDataSetChanged()
                setupProcessList()

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


    private fun setupAdapter() {

        adapter = ListsAdapter(activity!!, profiles, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mView.Lists.layoutManager = linearLayoutManager
        adapter.notifyDataSetChanged()
        mView.Lists.adapter = adapter


    }

    private fun setupProcessList() {

        processList = Array(3) { "" }
        processList[0] = (getString(R.string.text_ban))
        processList[1] = (getString(R.string.text_send_message))
        processList[2] = (getString(R.string.text_show_profile))


    }

    override fun onItemClick(view: View, position: Int) {
        //

        listPosition = position
        if (view.id == R.id.ItemSettings) {

            dialogHelper.process(processList, this, getString(R.string.text_choose_process))

        } else {

            showProfile()

        }
    }


    override fun onItemClicked(position: Int) {
        when (position) {

            0 -> {
                banUser()
            }
            1 -> {
                sendMessage()
            }
            2 -> {
                showProfile()
            }


        }
    }


    private fun banUser() {
        dialogHelper.progress()
        banHelper = BanHelper(activity!!, this)
        banHelper.ban(profiles[listPosition].id)

    }


    private fun sendMessage() {

        FragmentHelper.changeFragment("Chat", activity!!.supportFragmentManager,1)

    }

    private fun showProfile() {
        EventBus.getDefault().postSticky(EventBusHelper.sendProfile(profiles[listPosition]))
        FragmentHelper.changeFragment("Profile", activity!!.supportFragmentManager,1)
    }


    override fun completed() { // BAN user completed
        adapter.notifyItemChanged(listPosition)
        dialogHelper.progressDismiss()

    }


}