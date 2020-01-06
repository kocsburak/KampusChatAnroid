package com.xva.kampuschat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xva.kampuschat.R
import com.xva.kampuschat.adapters.DepartmentAdapter
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Department
import com.xva.kampuschat.entities.University
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.EventBusHelper
import com.xva.kampuschat.utils.FragmentHelper
import kotlinx.android.synthetic.main.fragment_department.*
import kotlinx.android.synthetic.main.fragment_department.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentFragment : Fragment(), Callback<List<Department>>,
    DepartmentAdapter.ItemClickListener {


    private lateinit var mView: View
    private lateinit var service: ApiService
    private lateinit var call: Call<List<Department>>
    private lateinit var list: List<Department>
    private lateinit var university: University
    private lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_department, container, false)
        service = RetrofitBuilder.createService(ApiService::class.java)
        dialogHelper = DialogHelper(activity!!)
        return mView
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendUniversity) {
        university = data.university
        getDepartments()

    }


    private fun getDepartments() {
        dialogHelper.progress()
        call = service.getDepartments(university.id)
        call.enqueue(this)

    }


    override fun onFailure(call: Call<List<Department>>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<List<Department>>, response: Response<List<Department>>) {
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

        var departmentAdapter = DepartmentAdapter(activity!!, list, this)
        var linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewDeparments.layoutManager = linearLayoutManager
        departmentAdapter.notifyDataSetChanged()
        mView.recyclerViewDeparments.adapter = departmentAdapter
    }


    override fun onItemClick(view: View, position: Int) {
        EventBus.getDefault().postSticky(EventBusHelper.sendDepartment(list[position]))
        FragmentHelper.changeFragment("Register", activity!!.supportFragmentManager)
    }


}