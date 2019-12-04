package com.xva.kampuschat.entities

class ApiError {


    constructor(message: String, errors: Map<String, List<String>>) {
        this.message = message
        this.errors = errors
    }

    var message: String
    var errors: Map<String, List<String>>

}