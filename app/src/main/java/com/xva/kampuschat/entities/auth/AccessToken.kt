package com.xva.kampuschat.entities.auth

import com.squareup.moshi.Json

class AccessToken {


    constructor(
        token_type: String,
        expires_in: String,
        access_token: String,
        refresh_token: String
    ) {
        this.token_type = token_type
        this.expires_in = expires_in
        this.access_token = access_token
        this.refresh_token = refresh_token
    }


    @Json(name = "token_type")
    var token_type: String


    @Json(name = "expires_in")
    var expires_in: String


    @Json(name = "access_token")
    var access_token: String


    @Json(name = "refresh_token")
    var refresh_token: String

}