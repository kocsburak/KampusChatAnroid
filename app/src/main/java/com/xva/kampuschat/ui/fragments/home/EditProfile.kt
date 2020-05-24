package com.xva.kampuschat.ui.fragments.home

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_editprofile.view.*
import kotlinx.android.synthetic.main.header_back.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException


class EditProfile : Fragment(), Callback<String> {


    private lateinit var mView: View
    private lateinit var apiService: ApiService
    private lateinit var dialogHelper: DialogHelper
    private lateinit var call: Call<String>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private var imageUri: Uri? = null
    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_editprofile, container, false)
        sharedPreferencesHelper =
            SharedPreferencesHelper(activity!!)
        dialogHelper = DialogHelper(activity!!)
        apiService =
            RetrofitBuilder.createServiceWithAuth(ApiService::class.java, sharedPreferencesHelper)

        mView.BackTextView.text = getString(R.string.text_edit_profile)
        mView.BackButton.setImageDrawable(activity!!.getDrawable(R.drawable.ic_check_circle_black_36dp))

        mView.BackButton.setOnClickListener {
            updateProfile()
        }

        mView.ChangeProfilePhoto.setOnClickListener {
            getImage()
        }



        setProfile()

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
        this.profile = data.profile!!
    }


    private fun setProfile() {


        if (profile.profile_photo_url != null) {
            mView.ProfilePhoto.setImageBitmap(PhotoHelper.getBitmap(profile.profile_photo_url!!))
        }


    }


    private fun getImage() {

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 100)
    }


    private fun updateProfile() {


        if (imageUri != null) {
            dialogHelper.progress()
            val imageStream = activity!!.contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)

            profile.profile_photo_url = PhotoHelper.getUrl(selectedImage)
            Log.e("Photo",profile.profile_photo_url)


        }

        if (imageUri != null) {

            call = apiService.updateProfile(
                sharedPreferencesHelper.getEvent().user_id,
                profile.profile_photo_url!!
            )
            call.enqueue(this)
        } else {
            getBack()
        }


    }


    override fun onFailure(call: Call<String>, t: Throwable) {
        Log.e("wsss","Calıstı")
        dialogHelper.progressDismiss()
        Toast.makeText(
            context,
            context!!.getString(R.string.error_something_wrong),
            Toast.LENGTH_LONG
        )
            .show()

    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

        if (response.isSuccessful) {
            EventBus.getDefault().postSticky(
                EventBusHelper.updateProfile(
                    profile.profile_photo_url!!
                )
            )
            getBack()
        }
        Log.e("wsss","Cal")

        dialogHelper.progressDismiss()
    }


    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)


        if (resultCode == RESULT_OK) {
            try {

                imageUri = data!!.data
                val imageStream = activity!!.contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                mView.ProfilePhoto.setImageBitmap(selectedImage)


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(
                    activity!!,
                    getString(R.string.error_something_wrong),
                    Toast.LENGTH_LONG
                ).show()
                imageUri = null
            }

        }
    }


    private fun getBack() {
        FragmentHelper.changeFragment("Settings", activity!!.supportFragmentManager,1)

    }


}