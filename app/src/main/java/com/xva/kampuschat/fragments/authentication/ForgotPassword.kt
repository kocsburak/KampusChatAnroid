package com.xva.kampuschat.fragments.authentication

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.FragmentHelper
import kotlinx.android.synthetic.main.fragment_forgotpassword.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : Fragment(), View.OnClickListener, Callback<String> {


    private lateinit var mView: View
    private lateinit var service: ApiService
    private lateinit var call: Call<String>
    private var responseCount = 0

    private lateinit var dialogHelper: DialogHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_forgotpassword, container, false)
        service = RetrofitBuilder.createService(ApiService::class.java)
        dialogHelper = DialogHelper(activity!!)
        mView.buttonDone.setOnClickListener(this)
        mView.link2.setOnClickListener(this)

        return mView
    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.buttonDone -> {
                if (responseCount == 0 && isEmailValid()) {
                    sendCode()
                } else {

                    if (validatePasswordAndCode()) {
                        updatePassword()
                    }

                }
            }

            R.id.link2 -> {
                loadLoginFragment()
            }

        }


    }

    private fun isEmailValid(): Boolean {


        if ((mView.editTextEmail.text.isEmpty()) || !(!TextUtils.isEmpty(mView.editTextEmail.text) && Patterns.EMAIL_ADDRESS.matcher(
                mView.editTextEmail.text
            ).matches())
        ) {
            mView.editTextEmail.error = getString(R.string.error_email_not_matched)
            return false
        }


        return true
    }


    private fun sendCode() {
        dialogHelper.progress()
        call = service.sendCode(mView.editTextEmail.text.toString())
        call.enqueue(this)

    }


    private fun validatePasswordAndCode(): Boolean {

        var error = 0

        when {
            mView.Password.text.isEmpty() -> {
                mView.Password.error = getString(R.string.error_field_blank)
                error++
            }
            mView.Password.text.toString() != mView.editTextConfirmPassword.text.toString() -> {
                mView.Password.error = getString(R.string.error_passwords_not_matched)
                error++
            }
            mView.Password.text.length < 6 -> {
                mView.Password.error = getString(R.string.error_at_least_6)
                error++
            }
        }

        if (error > 0) {
            return false
        }

        return true

    }


    private fun updatePassword() {
        dialogHelper.progress()
        call = service.updatePassword(
            mView.editTextEmail.text.toString(),
            mView.editTextCode.text.toString(),
            mView.Password.text.toString()
        )
        call.enqueue(this)
    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

        if (response.isSuccessful) {

            if (responseCount == 0) {
                // sendCode function response
                showCodeAndPasswordEdittexts()
                responseCount++
            } else {
                // updatePassword function response
               loadLoginFragment()
            }

        } else {

            handleErrors(response.code())

        }

       dialogHelper.progressDismiss()
    }


    private fun showCodeAndPasswordEdittexts() {
        mView.group.visibility = View.VISIBLE
        mView.editTextEmail.isEnabled = false
        mView.editTextEmail.alpha = 0.5f
    }


    private fun handleErrors(code: Int) {


        if (code == 404) {

            when (responseCount) {
                0 -> mView.editTextEmail.error = getString(R.string.error_email_not_found)
                1 -> mView.editTextCode.error = getString(R.string.error_code_invalid)

            }

            Toast.makeText(
                activity!!,
                getString(R.string.error_something_wrong),
                Toast.LENGTH_LONG
            )
                .show()


        }


    }


    private fun loadLoginFragment() {
        FragmentHelper.changeFragment("Login", activity!!.supportFragmentManager,1)
    }


}