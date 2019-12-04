package com.xva.kampuschat.entities

import com.squareup.moshi.Json

class University {


    @Json(name = "id")
    var id = 0

    @Json(name = "name")
    lateinit var name: String

    @Json(name = "email_type")
    lateinit var email_type: String

    @Json(name = "created_at")
    lateinit var created_at: String

    @Json(name = "updated_at")
    lateinit var updated_at: String


}