package com.xva.kampuschat.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.MainActivity
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.eventhelper.SetOnline
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.ui.adapters.home.SettingsAdapter
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_back.view.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsList : Fragment(), SettingsAdapter.ItemClickListener, Callback<String> {


    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<String>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var mView: View
    private lateinit var adapter: SettingsAdapter
    private lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_settings, container, false)

        setupList()
        setupAdapter()


        mView.BackTextView.text = getString(R.string.text_settings)
        mView.BackButton.setImageDrawable(activity!!.getDrawable(R.drawable.ic_person_pin_black_36dp))

        mView.BackButton.setOnClickListener {
            FragmentHelper.changeFragment("Profile", activity!!.supportFragmentManager, 0)
        }

        return mView
    }

    private fun setupList() {

        list = ArrayList()

        list.add(getString(R.string.text_edit_profile))
        list.add(getString(R.string.text_logout))


    }

    private fun setupAdapter() {

        adapter = SettingsAdapter(activity!!, list, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        mView.Lists.layoutManager = linearLayoutManager
        adapter.notifyDataSetChanged()
        mView.Lists.adapter = adapter


    }


    override fun onItemClick(position: Int) {
        when (position) {

            0 -> {
                FragmentHelper.changeFragment("EditProfile", activity!!.supportFragmentManager, 1)
            }

            1 -> {
                logout()
            }
        }
    }


    private fun logout() {


        dialogHelper = DialogHelper(activity!!)



        sharedPreferencesHelper =
            SharedPreferencesHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)

        call = apiService.logout()
        call.enqueue(this)
        dialogHelper.progress()

    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        dialogHelper.progressDismiss()
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if (response.isSuccessful) {
            dialogHelper.progressDismiss()

            EventBus.getDefault().postSticky(EventBusHelper.notificationService(false))
            //var onlineStatus = SetOnline(sharedPreferencesHelper)
            //onlineStatus.setOffline()


            sharedPreferencesHelper.deleteAccessToken()

            startActivity(Intent(activity!!, MainActivity::class.java))
            activity!!.finish()
        }
    }


}