package com.xva.kampuschat.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.adapters.ChatListAdapter

import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Profile
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.ILike
import com.xva.kampuschat.interfaces.IProcessDialog
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.LikeHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_back.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatList : Fragment()  , Callback<List<Profile>> , ChatListAdapter.ItemClickListener , IProcessDialog , ILike {



    private lateinit var mView: View
    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<List<Profile>>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var profiles: List<Profile>
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

            FragmentHelper.changeFragment("Shuffle",activity!!.supportFragmentManager,1)

        }

        getChats()
        return mView
    }



    private fun getChats(){

        dialogHelper.progress()
        call = apiService.getChats(sharedPreferencesHelper.getEvent().user_id)
        call.enqueue(this)
    }


    override fun onFailure(call: Call<List<Profile>>, t: Throwable) {
        dialogHelper.progressDismiss()
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
        if (response.isSuccessful) {

            if (response.code() == 200) {

                profiles = response.body()!!


                setupAdapter()
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

        adapter = ChatListAdapter(activity!!, profiles, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mView.Lists.layoutManager = linearLayoutManager
        adapter.notifyDataSetChanged()
        mView.Lists.adapter = adapter


    }

    private fun setupProcessList() {

        processList = Array(1) { "" }
        processList[0] = (getString(R.string.text_like))

    }


    override fun onItemClick(view: View, position: Int) {
        listPosition = position

        if(!profiles[position].liked_each_other){
            dialogHelper.process(processList, this, getString(R.string.text_choose_process))
        }
        
    }

    override fun onItemClicked(position: Int) {
       likeUser()
    }

    private fun likeUser(){
        dialogHelper.progress()
        var likeHelper = LikeHelper(activity!!,this)
        likeHelper.likeUser(profiles[listPosition].id)
    }

    override fun liked(code: Int) {

        if(code == 1){
            dialogHelper.matched(profiles[listPosition])
        }
        dialogHelper.progressDismiss()


    }


}