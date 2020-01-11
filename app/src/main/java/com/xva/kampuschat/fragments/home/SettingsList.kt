package com.xva.kampuschat.fragments.home

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
import com.xva.kampuschat.adapters.SettingsAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_lists.view.*
import kotlinx.android.synthetic.main.header_back.view.*
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



        mView.BackButton.setOnClickListener {
            FragmentHelper.changeFragment("Profile",activity!!.supportFragmentManager,0)
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
                FragmentHelper.changeFragment("EditProfile", activity!!.supportFragmentManager,1)
            }

            1 -> {
                logout()
            }
        }
    }


    private fun logout() {


        dialogHelper = DialogHelper(activity!!)
        sharedPreferencesHelper = SharedPreferencesHelper(activity!!)
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
            sharedPreferencesHelper.deleteAccessToken()
            startActivity(Intent(activity!!,MainActivity::class.java))
            activity!!.finish()
        }
    }


}