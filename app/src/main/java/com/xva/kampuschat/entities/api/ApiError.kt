package com.xva.kampuschat.entities.api

class ApiError {


    constructor(message: String, errors: Map<String, List<String>>) {
        this.message = message
        this.errors = errors
    }

    var message: String
    var errors: Map<String, List<String>>

}