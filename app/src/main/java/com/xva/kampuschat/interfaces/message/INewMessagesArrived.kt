package com.xva.kampuschat.interfaces.message

import com.xva.kampuschat.entities.home.Message

interface INewMessagesArrived {


    fun newMessagesHasArrived(messages:ArrayList<Message>?)


}