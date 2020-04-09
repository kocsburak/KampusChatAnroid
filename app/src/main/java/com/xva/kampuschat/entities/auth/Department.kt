package com.xva.kampuschat.entities.auth

import com.squareup.moshi.Json

class Department {


    @Json(name = "id")
    var id = 0

    @Json(name = "name")
    lateinit var name: String

    @Json(name = "created_at")
    lateinit var created_at: String

    @Json(name = "updated_at")
    lateinit var updated_at: String



}