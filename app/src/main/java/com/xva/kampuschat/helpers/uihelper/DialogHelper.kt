package com.xva.kampuschat.helpers.uihelper

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.helpers.listhelper.Ban
import com.xva.kampuschat.helpers.photohelper.PhotoHelper
import com.xva.kampuschat.interfaces.dialog.IDatePicker
import com.xva.kampuschat.interfaces.dialog.IProcessDialog
import com.xva.kampuschat.interfaces.dialog.IShuffleDialog
import com.xva.kampuschat.interfaces.process.IProcessCompleted
import kotlinx.android.synthetic.main.dialog_matched.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class DialogHelper(var context: Context) {


    private lateinit var progressDialog: ProgressDialog
    private lateinit var processDialog: AlertDialog
    private lateinit var shuffleDialog: Dialog
    private lateinit var matchedDialog: Dialog


    public fun progress() {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(context.getString(R.string.text_please_wait))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    public fun progressDismiss() {
        progressDialog.dismiss()
    }

    public fun process(processList: Array<String>, listener: IProcessDialog, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setItems(processList) { _, which ->
            listener.onItemClicked(which)
            processDismiss()
        }

        processDialog = builder.create()
        processDialog.show()

    }

    public fun processDismiss() {
        processDialog.dismiss()
    }

    public fun shuffle(user: Profile, listener: IShuffleDialog) {

        val inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.dialog_shuffle, null)
        var cancelButton = view.findViewById<ImageView>(R.id.CancelButton)
        var chatButton = view.findViewById<Button>(R.id.StartChatButton)

        var fullname = view.findViewById<TextView>(R.id.Fullname)
        var username = view.findViewById<TextView>(R.id.Username)
        var department = view.findViewById<TextView>(R.id.Department)
        var gender = view.findViewById<TextView>(R.id.Gender)
        var age = view.findViewById<TextView>(R.id.Age)


        if (user.profile_photo_url != null && user.profile_photo_url!!.isNotEmpty()) {

            var photo = view.findViewById<ImageView>(R.id.profilePhoto2)

            photo.setImageBitmap(PhotoHelper.getBitmap(user.profile_photo_url!!))


        }




        fullname.text = user.fullname
        username.text = user.username
        department.text = user.department_name

        if (user.gender == "M") {
            gender.text = context.getString(R.string.text_male)
        } else {
            gender.text = context.getString(R.string.text_female)
        }

        var count = getAge(user.date_of_birth)
        age.text = count.toString()


        cancelButton.setOnClickListener {
            shuffleDismiss()
        }


        chatButton.setOnClickListener {
            listener.onButtonClicked()
            shuffleDismiss()
        }

        shuffleDialog = Dialog(context)
        shuffleDialog.setContentView(view)

        shuffleDialog.setCancelable(false)
        shuffleDialog.show()

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


    public fun shuffleDismiss() {
        shuffleDialog.dismiss()
    }

    public fun matched(profile: Profile) {
        val inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.dialog_matched, null)
        var cancelButton = view.findViewById<ImageView>(R.id.cancelButton)

        cancelButton.setOnClickListener {
            matchedDismiss()
        }

        view.fullname.text = profile.fullname
        view.username.text = profile.username
        view.department.text = profile.department_name


        if (profile.profile_photo_url != null) {
            view.profilePhoto.setImageBitmap(PhotoHelper.getBitmap(profile!!.profile_photo_url!!))
        }

        matchedDialog = Dialog(context)
        matchedDialog.setContentView(view)
        matchedDialog.setCancelable(false)
        matchedDialog.show()


    }

    public fun matchedDismiss() {
        matchedDialog.dismiss()
    }

    public fun datePicker(listener: IDatePicker) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView

                var monthDate = "" + (monthOfYear + 1)
                var dayDate = dayOfMonth.toString()

                if (monthOfYear + 1 < 10) {

                    monthDate = "0" + (monthOfYear + 1)

                }

                if (dayOfMonth < 10) {
                    dayDate = "0$dayOfMonth"
                }


                listener.onSelectTime(year.toString(), monthDate, dayDate)

            },
            year,
            month,
            day
        )
        picker.show()

    }


    public fun banUserAlertDialog(user_id: Number,listener:IProcessCompleted) {

        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(context.getString(R.string.text_ban_user_title))

        // Display a message on alert dialog
        builder.setMessage(context.getString(R.string.text_ban_user_message))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(context.getString(R.string.text_yes)) { dialog, which ->

            var helper = Ban(context, listener)
            helper.ban(user_id)
            dialog.dismiss()
            progress()

        }


        // Display a negative button on alert dialog
        builder.setNegativeButton(context.getString(R.string.text_no)) { dialog, which ->
            dialog.dismiss()
        }


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
        dialog.setCancelable(false)


    }




}


