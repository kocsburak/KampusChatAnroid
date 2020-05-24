package com.xva.kampuschat.helpers.datahelper

import com.xva.kampuschat.entities.auth.Department
import com.xva.kampuschat.entities.auth.Profile
import com.xva.kampuschat.entities.auth.University
import com.xva.kampuschat.entities.home.Chat
import com.xva.kampuschat.entities.home.Message

class EventBusHelper {

    // Authentication
    internal class sendUniversity(var university: University)
    internal class sendDepartment(var department: Department)

    // Fragment
    internal class publishFragment(var name: String)

    // Profile
    internal class sendProfile(var profile: Profile?)
    internal class updateProfile(var url: String)

    // Notifacation
    internal class updateNotificationPermission(var permission: Boolean)
    internal class notificationService(var status: Boolean)

    // Message

    // From Notifacation Service
    internal class sendMessages(var messages: ArrayList<Message>?)

    // From Message Service
    internal class messages(var messages: ArrayList<Message>?)

    internal class sendMessagesToServiceForSending(var message: Message)
    internal class sendPhotoPartForMessage(var part:String)

    // Chat
    internal class sendChatInformations(var chat: Chat)
    internal class sendChatIds(var chat_id: Number, var owner_id: Number, var guest_id: Number)


    // Online
    internal class onlineStatus(var status:Boolean)

    // Typing
    internal class typingStatus(var status: Boolean?)
    internal class sendTypingStatus(var status: Boolean?)


    // Seen

    internal class isSended(var id:Int)
    internal class updateSeenValue(var id:Int)

    // Dialog
    internal class progress(var status: Boolean)


}