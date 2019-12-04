package com.xva.kampuschat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.activities.HomeActivity
import com.xva.kampuschat.api.ApiErrorHelper
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.utils.SharedPreferencesHelper
import com.xva.kampuschat.entities.AccessToken
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.FragmentHelper
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(), View.OnClickListener, Callback<AccessToken> {


    private lateinit var mView: View
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var service: ApiService
    private lateinit var call: Call<AccessToken>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_login, container, false)
        preferencesHelper = SharedPreferencesHelper(activity!!)
        service = RetrofitBuilder.createService(ApiService::class.java)
        mView.buttonLogin.setOnClickListener(this)
        mView.textViewLink.setOnClickListener(this)


        return mView
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.buttonLogin -> {
                login()
            }

            R.id.textViewLink -> {
                loadUniversityFragment()
            }

        }
    }


    private fun login() {
        call = service.login(editTextUsername.text.toString(), editTextPassword.text.toString())
        call.enqueue(this)
    }


    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
        Toast.makeText(activity!!, t.message, Toast.LENGTH_LONG).show()
    }

    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

        if (response.isSuccessful) {
            preferencesHelper.saveAccessToken(response.body()!!)
            startActivity(Intent(activity!!, HomeActivity::class.java))
            activity!!.finish()

        } else {
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
                        view!!.editTextUsername.error = getString(R.string.error_field_blank)
                    }

                    if (error.key == "password") {
                        view!!.editTextPassword.error = getString(R.string.error_field_blank)
                    }


                }

                return
            }

        }
        // ELSE 404 , 500 vs ...
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show()


    }


    private fun loadUniversityFragment(){
        FragmentHelper.changeFragment("University",activity!!.supportFragmentManager)
    }



}