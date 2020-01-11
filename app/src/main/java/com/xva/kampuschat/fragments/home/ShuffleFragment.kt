package com.xva.kampuschat.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.xva.kampuschat.R
import com.xva.kampuschat.api.RetrofitBuilder
import com.xva.kampuschat.entities.Profile

import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.interfaces.IProcessCompleted
import com.xva.kampuschat.interfaces.IShuffleDialog
import com.xva.kampuschat.utils.DialogHelper
import com.xva.kampuschat.utils.FragmentHelper
import com.xva.kampuschat.utils.SharedPreferencesHelper
import com.xva.kampuschat.utils.StartChatHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShuffleFragment : Fragment(), View.OnClickListener, Callback<Profile>, IShuffleDialog,
    IProcessCompleted {


    private lateinit var mView: View
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var apiService: ApiService
    private lateinit var call: Call<Profile>
    private lateinit var dialogHelper: DialogHelper
    private lateinit var startChatHelper: StartChatHelper


    private var shuffle_count = 3
    private var matched_user: Profile? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_shuffle, container, false)

        var chatButton = mView.findViewById<ImageView>(R.id.ChatListButton)
        chatButton.setOnClickListener(this)


        var shuffleButton = mView.findViewById<ImageView>(R.id.ShuffleButton)
        shuffleButton.setOnClickListener(this)


        sharedPreferencesHelper = SharedPreferencesHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)
        dialogHelper = DialogHelper(activity!!)
        determinateShuffleCount()

        return mView
    }


    private fun determinateShuffleCount() {


        var group = sharedPreferencesHelper.getEvent().group

        if (group <= 3) {
            shuffle_count -= group

            if (shuffle_count < 0) {
                shuffle_count *= -1
            }
        }

    }


    private fun shuffle() {

        if (shuffle_count > 0) {

            dialogHelper.progress()
            call = apiService.shuffle(sharedPreferencesHelper.getEvent().user_id)
            call.enqueue(this)

        } else {

            Toast.makeText(
                activity!!,
                getString(R.string.text_shuffle_right_is_done),
                Toast.LENGTH_LONG
            ).show()

        }
    }


    private fun sendUserToDialog() {

        dialogHelper.shuffle(matched_user!!, this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ChatListButton -> {
                FragmentHelper.changeFragment("ChatList", activity!!.supportFragmentManager,1)
            }

            R.id.ShuffleButton -> {

                shuffle()
            }
        }
    }


    override fun onFailure(call: Call<Profile>, t: Throwable) {
        Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
            .show()
    }

    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {

        if (response.isSuccessful) {

            if (response.code() == 200) { // somebody is found

                shuffle_count--
                matched_user = response.body()!!
                sendUserToDialog()

            }

            if (response.code() == 204) { // nobody is found
                Toast.makeText(
                    activity!!,
                    getString(R.string.error_nobody_is_found),
                    Toast.LENGTH_LONG
                ).show()

            }


        } else {
            Toast.makeText(activity!!, getString(R.string.error_something_wrong), Toast.LENGTH_LONG)
                .show()
        }

        dialogHelper.progressDismiss()

    }


    override fun onButtonClicked() {
        dialogHelper.shuffleDismiss()
        dialogHelper.progress()
        startChatHelper = StartChatHelper(activity!!, this)
        startChatHelper.startChat(matched_user!!.id)

    }


    override fun completed() {
        dialogHelper.progressDismiss()
        FragmentHelper.changeFragment("Chat",activity!!.supportFragmentManager,1)
    }

}

// TODO : api shuffle kendimiz ile eşleşme sorunu
// TODO : nedeni first metodu yüzünden ilk kayıdı getiriyor get yap onu