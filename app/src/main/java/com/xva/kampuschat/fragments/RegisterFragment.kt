package com.xva.kampuschat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.activities.HomeActivity
import com.xva.kampuschat.api.ApiErrorHelper
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.AccessToken
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IDatePicker
import com.xva.kampuschat.interfaces.IProcessDialog
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.EventBusHelper
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_register.view.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment(), View.OnClickListener, Callback<AccessToken>, IProcessDialog,
    IDatePicker {


    private lateinit var mView: View
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var dialogHelper: DialogHelper
    private lateinit var service: ApiService
    private lateinit var call: Call<AccessToken>
    private var departmentId = -1
    private var universityEmailType = ""
    private var isGenderSelected = false
    private var isDateOfBirthSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_register, container, false)
        preferencesHelper = SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        service = RetrofitBuilder.createService(ApiService::class.java)

        mView.buttonDone.setOnClickListener(this)
        mView.link.setOnClickListener(this)
        mView.textViewGender.setOnClickListener(this)
        mView.textViewDateOfBirth.setOnClickListener(this)

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
    internal fun onDataEvent(data: EventBusHelper.sendDepartment) {
        departmentId = data.department.id
    }

    @Subscribe(sticky = true)
    internal fun onDataEvent(data: EventBusHelper.sendUniversity) {
        this.universityEmailType = data.university.email_type
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonDone -> {
                register()
            }
            R.id.link -> {
                loadLoginFragment()
            }
            R.id.textViewGender -> {
                loadGender()
            }

            R.id.textViewDateOfBirth -> {
                dialogHelper.datePicker(this)
            }

        }
    }


    private fun register() {
// TODO : DATE OF BIRTH DUZENLE
        if (validate()) {
            dialogHelper.progress()
            call = service.register(
                mView.editTextFullname.text.toString(),
                mView.editTextEmail.text.toString(),
                mView.Username.text.toString(),
                mView.Password.text.toString(),
                getGender(),
                mView.textViewDateOfBirth.text.toString(),
                departmentId
            )
            call.enqueue(this)
        }

    }


    private fun validate(): Boolean {

        var error = 0

        if (mView.editTextFullname.length() < 2) {
            mView.editTextFullname.error = getString(R.string.error_at_least_2)
            error++
        }

        if ((mView.editTextEmail.text.isEmpty()) || !(!TextUtils.isEmpty(mView.editTextEmail.text) && Patterns.EMAIL_ADDRESS.matcher(
                mView.editTextEmail.text
            ).matches()) || universityEmailType !in mView.editTextEmail.text.toString()
        ) {
            mView.editTextEmail.error = getString(R.string.error_email_not_matched)
            error++
        }

        if (mView.Username.length() < 2) {
            mView.Username.error = getString(R.string.error_at_least_2)
            error++
        }

        if (mView.Password.length() < 6) {
            mView.Password.error = getString(R.string.error_at_least_6)
            error++
        }

        if (mView.Password.text.toString() != mView.editTextConfirmPassword.text.toString()) {
            mView.Password.error = getString(R.string.error_passwords_not_matched)
            mView.editTextConfirmPassword.error = getString(R.string.error_passwords_not_matched)
            error++
        }

        if (!isGenderSelected) {
            mView.textViewGender.error = getString(R.string.error_select_gender)
            error++
        }

        if (!isDateOfBirthSelected) {
            mView.textViewDateOfBirth.error = getString(R.string.error_select_date_of_birth)
            error++
        }


        if (error == 0 && departmentId != -1) {
            return true
        }

        return false

    }


    private fun getGender(): String {

        if (mView.textViewGender.text == getString(R.string.text_male)) {
            return "M"
        }

        return "F"
    }


    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
        if (response.isSuccessful) {
            preferencesHelper.saveAccessToken(response.body()!!)
            preferencesHelper.saveEmail(mView.editTextEmail.text.toString())
            startActivity(Intent(activity!!, HomeActivity::class.java))
            activity!!.finish()

        } else {
            Log.e("Body", response.errorBody().toString())
            handleErrors(response.errorBody(), response.code())
        }

        dialogHelper.progressDismiss()

    }


    private fun handleErrors(response: ResponseBody?, code: Int) {

        when (code) {

            422 -> {

                var apiError = ApiErrorHelper.convertErrors(response!!)

                for (error: Map.Entry<String, List<String>> in apiError!!.errors.entries) {

                    if (error.key == "username") {
                        view!!.Username.error = getString(R.string.error_username_taken)
                    }

                    if (error.key == "email") {
                        view!!.editTextEmail.error = getString(R.string.error_email_taken)
                    }

                    Log.e("Key", error.key)
                }

                return
            }

        }
        // ELSE 404 , 500 vs ...
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()


    }


    private fun loadGender() {
        var genders = Array<String>(2) { "" }
        genders[0] = getString(R.string.text_male)
        genders[1] = getString(R.string.text_female)
        dialogHelper.process(genders, this, getString(R.string.text_select_gender))
    }

    override fun onItemClicked(position: Int) {
        isGenderSelected = true
        if (position == 0) {
            mView.textViewGender.text = getString(R.string.text_male)
            return
        }
        mView.textViewGender.text = getString(R.string.text_female)
    }


    private fun loadLoginFragment() {
        FragmentHelper.changeFragment("Login", activity!!.supportFragmentManager)
    }


    @SuppressLint("SetTextI18n")
    override fun onSelectTime(year: Int, month: Int, day: Int) {
        mView.textViewDateOfBirth.text = "$year-$month-$day"
        isDateOfBirthSelected = true
    }


}