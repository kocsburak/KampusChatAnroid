package com.xva.kampuschat.interfaces.message

import com.xva.kampuschat.entities.home.Message

interface IAllMessageArrived {


    fun allMessagesHasArrived(messages:ArrayList<Message>?)


}