package com.xva.kampuschat.entities

import com.squareup.moshi.Json

class Event {



    @Json(name = "id")
    var id = 0

    @Json(name = "user_id")
    var user_id = 0

    @Json(name = "is_online")
    var is_online = false

    @Json(name = "group")
    var group = 0

    @Json(name = "last_seen_at")
    lateinit var last_seen_at : String

    @Json(name = "created_at")
    lateinit var created_at : String

    @Json(name = "updated_at")
    lateinit var updated_at : String

}