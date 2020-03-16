package com.xva.kampuschat.entities

import com.squareup.moshi.Json

class Chat {


    @Json(name = "id")
    var id = 0

    @Json(name = "owner_user_id")
    var owner_user_id = 0

    @Json(name = "guest_user_id")
    var guest_user_id = -1

    @Json(name = "is_checked")
    var is_checked = false

    @Json(name = "is_owner_typing")
    var is_owner_typing = false

    @Json(name = "is_guest_typing")
    var is_guest_typing = false

    @Json(name = "fullname")
    lateinit var fullname:String

    @Json(name = "profile_photo_url")
    var profile_photo_url:String? = null

    @Json(name = "created_at")
    lateinit var created_at:String

    @Json(name = "updated_at")
    lateinit var updated_at:String

    @Json(name = "department_name")
    var department_name:String? = null

    @Json(name = "did_user_banned_me")
    var did_user_banned_me: Boolean = false

    @Json(name = "did_i_banned_user")
    var did_i_banned_user: Boolean = false

    @Json(name = "liked_each_other")
    var liked_each_other: Boolean=false

    var notification_signal = 0
    var message_update_time = "2020-01-01 00:00:00"
    var last_message = ""


}