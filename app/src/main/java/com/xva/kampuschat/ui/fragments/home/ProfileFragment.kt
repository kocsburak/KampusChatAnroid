package com.xva.kampuschat.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.helpers.datahelper.EventBusHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import com.xva.kampuschat.helpers.photohelper.PhotoHelper
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.uihelper.FragmentHelper
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.header_profile.view.*
import kotlinx.android.synthetic.main.header_shuffle.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment(var code: Int) : Fragment(), Callback<Profile> {


    private lateinit var mView: View
    private var profile: Profile? = null
    private lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_profile, container, false)
        dialogHelper = DialogHelper(activity!!)
        checkProfile()

        mView.Button.setImageDrawable(activity!!.getDrawable(R.drawable.ic_recycler_settings))

        mView.Button.setOnClickListener {

            FragmentHelper.changeFragment("Settings",activity!!.supportFragmentManager,1)

        }

        return mView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendProfile) {
        if(code == 1){
            this.profile = data.profile
        }

    }


    private fun checkProfile() {

        if (profile != null ) {
            setInformations()
            setPP(1)
        } else {

            dialogHelper.progress()
            getProfile()

        }


    }


    private fun setInformations() {
        var fullname = mView.findViewById<TextView>(R.id.Fullname)
        var username = mView.findViewById<TextView>(R.id.Username)
        var department = mView.findViewById<TextView>(R.id.Department)
        var email = mView.findViewById<TextView>(R.id.Email)
        var gender = mView.findViewById<TextView>(R.id.Gender)
        var age = mView.findViewById<TextView>(R.id.Age)

        fullname.text = profile!!.fullname
        username.text = profile!!.username
        department.text = profile!!.department_name
        email.text = profile!!.email

        if (profile!!.gender == "M") {
            gender.text = getString(R.string.text_male)
        } else {
            gender.text = getString(R.string.text_female)
        }

        var count = getAge( profile!!.date_of_birth)
        age.setText(count.toString())

    }


    private fun getAge(dobString: String): Int {

        var date: Date? = null
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date == null) return 0

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.setTime(date)

        val year = dob.get(Calendar.YEAR)
        val month = dob.get(Calendar.MONTH)
        val day = dob.get(Calendar.DAY_OF_MONTH)

        dob.set(year, month + 1, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }



        return age
    }


    private fun getProfile() {
        var sharedPreferencesHelper =
            SharedPreferencesHelper(activity!!)
        var service =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        var call = service.getUser()
        call.enqueue(this)

    }



    private fun setPP(code:Int) {

        if(code == 1  && profile!!.profile_photo_url != null){
            mView.ProfilePhoto.setImageBitmap(PhotoHelper.getBitmap(profile!!.profile_photo_url!!))
        }
        if(code == 0 && profile!!.profile_photo_url != null){
            mView.ProfilePhoto.setImageBitmap(PhotoHelper.getBitmap(profile!!.profile_photo_url!!))
        }




    }


    override fun onFailure(call: Call<Profile>, t: Throwable) {
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {

        if (response.isSuccessful) {
            this.profile = response.body()!!
            dialogHelper.progressDismiss()
            setInformations()
            setPP(0)
            publishProfile()
        }

    }

    fun publishProfile() {
        EventBus.getDefault().postSticky(EventBusHelper.sendProfile(this.profile!!))
    }


}