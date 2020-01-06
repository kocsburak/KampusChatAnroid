package com.xva.kampuschat.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.adapters.UniversityAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.University
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.EventBusHelper
import com.xva.kampuschat.utils.FragmentHelper
import kotlinx.android.synthetic.main.fragment_university.*
import kotlinx.android.synthetic.main.fragment_university.view.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UniversityFragment : Fragment(), Callback<List<University>>,
    UniversityAdapter.ItemClickListener {


    private lateinit var mView: View
    private lateinit var service: ApiService
    private lateinit var call: Call<List<University>>
    private lateinit var list: List<University>
    private lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_university, container, false)
        service = RetrofitBuilder.createService(ApiService::class.java)
        dialogHelper = DialogHelper(activity!!)
        getUniversities()
        return mView
    }


    private fun getUniversities() {
        dialogHelper.progress()
        call = service.getUniversities()
        call.enqueue(this)
    }


    override fun onFailure(call: Call<List<University>>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<List<University>>, response: Response<List<University>>) {
        if (response.isSuccessful) {
            list = response.body()!!
            setupAdapter()
        } else {

            Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
                .show()
        }

        dialogHelper.progressDismiss()
    }


    private fun setupAdapter() {

        var universityAdapter = UniversityAdapter(activity!!, list, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewUniversity.layoutManager = linearLayoutManager
        universityAdapter.notifyDataSetChanged()
        mView.recyclerViewUniversity.adapter = universityAdapter
    }


    override fun onItemClick(view: View, position: Int) {

        EventBus.getDefault().postSticky(EventBusHelper.sendUniversity(list[position]))
        FragmentHelper.changeFragment("Department",activity!!.supportFragmentManager)

    }


}