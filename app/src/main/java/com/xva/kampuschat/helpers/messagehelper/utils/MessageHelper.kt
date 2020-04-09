package com.xva.kampuschat.helpers.messagehelper.utils

import com.xva.kampuschat.entities.home.Message
import com.xva.kampuschat.helpers.datahelper.SharedPreferencesHelper
import java.text.SimpleDateFormat

class MessageHelper {


    companion object{
        fun setupArrayList(
            list: List<Message>,
            sharedPreferencesHelper: SharedPreferencesHelper
        ): ArrayList<Message> {

            var messageList = ArrayList<Message>()

            for (item in list) {

                compareDates(
                    item.created_at,
                    sharedPreferencesHelper
                )
                messageList.add(item)
            }


            return messageList

        }


        private fun compareDates(date: String, sharedPreferencesHelper: SharedPreferencesHelper) {

            val dateFormat = SimpleDateFormat("y-M-d H:m:s")
            var date1 = dateFormat.parse(date)
            var date2 = dateFormat.parse(sharedPreferencesHelper.getMessageLastDate())

            if (date1.after(date2)) {
                sharedPreferencesHelper.saveMessageLastDate(date)
            }

        }

    }




}