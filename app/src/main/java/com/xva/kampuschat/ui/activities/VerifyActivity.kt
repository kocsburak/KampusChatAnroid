package com.xva.kampuschat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.interfaces.api.ApiService
import com.xva.kampuschat.helpers.uihelper.DialogHelper
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_verify.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyActivity : AppCompatActivity(), Callback<String> {


    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var dialogHelper: DialogHelper

    private lateinit var service: ApiService
    private lateinit var call: Call<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        sharedPreferences = SharedPreferencesHelper(this)
        dialogHelper = DialogHelper(this)
        service =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferences)

        DoneButton.setOnClickListener {

            if (validateCode()) {
                dialogHelper.progress()
                verifyEmail()
            }


        }

    }


    private fun validateCode(): Boolean {

        if (Code.text.length != 8) {

            Code.error = getString(R.string.error_code_invalid)
            return false

        }
        Code.error = null

        return true


    }


    private fun verifyEmail() {
        call = service.verifyEmail(sharedPreferences.getEmail(),Code.text.toString())
        call.enqueue(this)

    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        Toast.makeText(this, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
        dialogHelper.progressDismiss()
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if (response.isSuccessful) {
            startActivity(Intent(this,HomeActivity::class.java))
            this.finish()
        }else{

            Code.error = getString(R.string.error_code_invalid)
        }
        dialogHelper.progressDismiss()
    }


}
