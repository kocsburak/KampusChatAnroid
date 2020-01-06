package com.xva.kampuschat.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.xva.kampuschat.R
import com.xva.kampuschat.entities.Profile
import com.xva.kampuschat.entities.User
import com.xva.kampuschat.interfaces.IDatePicker
import com.xva.kampuschat.interfaces.IProcessDialog
import com.xva.kampuschat.interfaces.IShuffleDialog
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

    public fun process(processList: Array<String>, listener: IProcessDialog,title:String) {
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

    public fun shuffle(user: User, listener: IShuffleDialog) {

        val inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.dialog_shuffle, null)
        var cancelButton = view.findViewById<ImageView>(R.id.CancelButton)
        var chatButton = view.findViewById<Button>(R.id.StartChatButton)

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


        matchedDialog = Dialog(context)
        matchedDialog.setContentView(view)
        matchedDialog.setCancelable(false)
        matchedDialog.show()


    }

    public fun matchedDismiss() {
        matchedDialog.dismiss()
    }


    public fun datePicker(listener:IDatePicker){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Display Selected date in TextView

            listener.onSelectTime(year,monthOfYear,dayOfMonth)

        }, year, month, day)
        picker.show()

    }




    }


