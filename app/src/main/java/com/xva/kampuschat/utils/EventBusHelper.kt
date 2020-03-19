package com.xva.kampuschat.utils

import com.xva.kampuschat.entities.*

class EventBusHelper {


    internal class sendUniversity(var university: University)
    internal class sendDepartment(var department:Department)
    internal class publishFragment(var name:String)
    internal class sendProfile(var profile: Profile?)
    internal class updateProfile(var url:String)
    internal class updateNotificationPermission(var permission : Boolean)
    internal class sendMessages(var messages:ArrayList<Message>?)
    internal class sendMessagesToService(var messages: ArrayList<Message>?,var processCode:Int)
    internal class sendChatInformations(var chat:Chat)
    internal class isTyping(var statue:Boolean)

}