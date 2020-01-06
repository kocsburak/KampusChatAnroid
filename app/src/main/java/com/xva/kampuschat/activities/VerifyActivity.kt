package com.xva.kampuschat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xva.kampuschat.R
import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
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
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if (response.code() == 204) {
            startActivity(Intent(this,HomeActivity::class.java))
        }else{
            Code.error = getString(R.string.error_code_invalid)
        }
    }


}
