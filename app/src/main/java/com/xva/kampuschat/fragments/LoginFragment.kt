package com.xva.kampuschat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.api.ApiErrorHelper
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.AccessToken
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IVerify
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import com.xva.kampuschat.utils.VerifyHelper
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(), View.OnClickListener, Callback<AccessToken>, IVerify {


    private lateinit var mView: View
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var dialogHelper: DialogHelper
    private lateinit var service: ApiService
    private lateinit var call: Call<AccessToken>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_login, container, false)
        preferencesHelper = SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        service = RetrofitBuilder.createService(ApiService::class.java)

        mView.LoginButton.setOnClickListener(this)
        mView.Link.setOnClickListener(this)
        mView.ForgotPassword.setOnClickListener(this)

        return mView
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.LoginButton -> {
                dialogHelper.progress()
                login()
            }

            R.id.Link -> {
                loadUniversityFragment()
            }

            R.id.ForgotPassword -> {
                loadForgotPasswordFragment()
            }

        }
    }


    private fun login() {
        call = service.login(Username.text.toString(), Password.text.toString())
        call.enqueue(this)
    }


    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

        if (response.isSuccessful) {
            preferencesHelper.saveAccessToken(response.body()!!)
            checkUserVerify()
        } else {
            dialogHelper.progressDismiss()
            handleErrors(response.errorBody(), response.code())
        }

    }


    private fun handleErrors(response: ResponseBody?, code: Int) {

        when (code) {

            401 -> {
                Toast.makeText(
                    activity!!,
                    getString(R.string.error_credentials_invalid),
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            422 -> {

                var apiError = ApiErrorHelper.convertErrors(response!!)

                for (error: Map.Entry<String, List<String>> in apiError!!.errors.entries) {

                    if (error.key == "username") {
                        view!!.Username.error = getString(R.string.error_field_blank)
                    }

                    if (error.key == "password") {
                        view!!.Password.error = getString(R.string.error_field_blank)
                    }


                }

                return
            }

        }
        // ELSE 404 , 500 vs ...
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()


    }


    private fun checkUserVerify() {
        var verifyHelper = VerifyHelper(activity!!, this)
        verifyHelper.checkUserVerify()
    }


    private fun loadUniversityFragment() {
        FragmentHelper.changeFragment("University", fragmentManager!!)
    }

    private fun loadForgotPasswordFragment() {

        FragmentHelper.changeFragment("ForgotPassword", fragmentManager!!)
    }

    override fun done() {
        dialogHelper.progressDismiss()
        activity!!.finish()
    }


}