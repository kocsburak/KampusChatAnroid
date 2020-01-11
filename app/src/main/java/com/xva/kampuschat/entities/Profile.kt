package com.xva.kampuschat.entities

import com.squareup.moshi.Json

class Profile {


    @Json(name = "id")
    var id = 0

    @Json(name = "department_id")
    var department_id = 0


    @Json(name = "email")
    lateinit var email:String

    @Json(name = "username")
    lateinit var username:String

    @Json(name = "fullname")
    lateinit var fullname:String

    @Json(name = "gender")
    lateinit var gender:String

    @Json(name = "date_of_birth")
    lateinit var date_of_birth:String

    @Json(name = "email_verified_at")
    var email_verified_at : String? = null

    @Json(name = "bio")
    var bio:String? = null

    @Json(name = "profile_photo_url")
    var profile_photo_url:String? = null

    @Json(name = "created_at")
    lateinit var created_at:String

    @Json(name = "updated_at")
    lateinit var updated_at:String

    @Json(name = "department_name")
    var department_name:String? = null
    @Json(name = "did_user_ban_me")
    var did_user_ban_me: Boolean = false

    @Json(name = "did_i_ban_user")
    var did_i_ban_user: Boolean = false

    @Json(name = "liked_each_other")
    var liked_each_other: Boolean=false








}