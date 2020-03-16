package com.xva.kampuschat.utils

import com.xva.kampuschat.entities.*

class EventBusHelper {


    internal class sendUniversity(var university: University)
    internal class sendDepartment(var department:Department)
    internal class publishFragment(var name:String)
    internal class sendProfile(var profile: Profile?)
    internal class updateProfile(var url:String)
    internal class updateNotificationPermission(var permission : Boolean)
    internal class sendNewMessages(var messages:ArrayList<Message>)
    internal class sendChatInformations(var chat:Chat)

}