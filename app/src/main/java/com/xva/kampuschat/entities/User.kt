package com.xva.kampuschat.entities

import com.squareup.moshi.Json

class User {


    constructor(
        id: Number,
        fullname: String,
        email: String,
        username: String,
        gender: String,
        date_of_birth: String,
        department_id: Int,
        is_verified: Boolean,
        bio: String,
        profile_photo_url: String,
        created_at: String,
        updated_at: String
    ) {
        this.id = id
        this.fullname = fullname
        this.email = email
        this.username = username
        this.gender = gender
        this.date_of_birth = date_of_birth
        this.department_id = department_id
        this.is_verified = is_verified
        this.bio = bio
        this.profile_photo_url = profile_photo_url
        this.created_at = created_at
        this.updated_at = updated_at
    }


    @Json(name = "id")
    var id: Number = 0

    @Json(name = "fullname")
    var fullname: String

    @Json(name = "email")
    var email: String

    @Json(name = "username")
    var username: String

    @Json(name = "gender")
    var gender: String

    @Json(name = "date_of_birth")
    var date_of_birth: String

    @Json(name = "department_id")
    var department_id: Int = 1

    @Json(name = "is_verified")
    var is_verified = false

    @Json(name = "bio")
    var bio: String

    @Json(name = "profile_photo_url")
    var profile_photo_url: String


    @Json(name = "created_at")
    var created_at: String


    @Json(name = "updated_at")
    var updated_at: String


}