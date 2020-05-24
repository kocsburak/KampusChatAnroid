package com.xva.kampuschat.entities.home

import com.squareup.moshi.Json

class Message {



    @Json(name = "id")
    var id = 0

    @Json(name = "chat_id")
    var chat_id = 0

    @Json(name = "type")
    lateinit var type : String

    @Json(name = "message")
    lateinit var message : String

    @Json(name = "sender_user_id")
    var sender_user_id = 0

    @Json(name = "is_seen")
    var is_seen = false

    @Json(name = "created_at")
    lateinit var created_at : String

    @Json(name = "updated_at")
    lateinit var updated_at : String

    var is_process_completed = false
    var is_sended = true


}