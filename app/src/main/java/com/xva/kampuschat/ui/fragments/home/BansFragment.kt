package com.xva.kampuschat.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.ui.adapters.home.ListsAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.interfaces.process.IProcessCompleted
import com.xva.kampuschat.interfaces.dialog.IProcessDialog
import com.xva.kampuschat.helpers.listhelper.Ban
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_shuffle.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BansFragment : Fragment(), Callback<List<Profile>>, ListsAdapter.ItemClickListener,
    IProcessDialog, IProcessCompleted {


    private lateinit var mView: View
    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<List<Profile>>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var profiles: ArrayList<Profile>
    private lateinit var banHelper: Ban
    private lateinit var adapter: ListsAdapter

    private var listPosition = 0

    private lateinit var processList: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_lists, container, false)
        sharedPreferencesHelper =
            SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        profiles = ArrayList()
        setClicks()
        getBans()
        return mView
    }

    private fun setClicks() {

        mView.Button.setOnClickListener {

            FragmentHelper.changeFragment("ChatList", activity!!.supportFragmentManager, 1,"Bans")
        }

    }


    private fun getBans() {
        dialogHelper.progress()
        call = apiService.getBans(sharedPreferencesHelper.getEvent().user_id)
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

                var list = response.body()!!

                setupArrayList(list)
                setupAdapter()
                setupProcessList()

            }

            if (response.code() == 204) {
                showListEmptyScreen()
            }


        }

        dialogHelper.progressDismiss()
    }


    private fun setupArrayList(list: List<Profile>) {

        for (item in list) {
            profiles.add(item)
        }

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

        processList = Array(1) { "" }
        processList[0] = (getString(R.string.text_remove_ban))


    }

    override fun onItemClick(view: View, position: Int) {
        //

        listPosition = position
        if (view.id == R.id.ItemSettings) {

            dialogHelper.process(processList, this, getString(R.string.text_choose_process))

        }
    }


    override fun onItemClicked(position: Int) {
        when (position) {

            0 -> {
                removeBanUser()
            }

        }
    }


    private fun removeBanUser() {
        dialogHelper.progress()
        banHelper = Ban(activity!!, this)
        banHelper.removeBan(profiles[listPosition].id)

    }


    override fun completed() { // BAN user completed
        profiles.removeAt(listPosition)
        adapter.notifyDataSetChanged()
        dialogHelper.progressDismiss()


        if(profiles.size == 0){

            showListEmptyScreen()

        }


    }


    private fun showListEmptyScreen(){


        mView.Lists.visibility = View.GONE
        mView.ListsEmptyBackground.visibility = View.VISIBLE
        mView.imageView3.visibility = View.VISIBLE
        mView.ListEmptyText.visibility = View.VISIBLE


    }


}