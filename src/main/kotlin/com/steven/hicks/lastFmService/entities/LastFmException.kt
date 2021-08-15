package com.steven.hicks.lastFmService.entities

import com.steven.hicks.lastFmService.controllers.dtos.ErrorResponse
import org.springframework.http.HttpStatus
import kotlin.properties.Delegates

@Suppress("MagicNumber")
class LastFmException : Exception {

    companion object {
        fun getTypeAndStatus(code: Int): Pair<ErrorResponse, HttpStatus> {
            return when (code) {
                // Bad requests
                4000 -> Pair(ErrorResponse(4000, "Username does not exist"), HttpStatus.BAD_REQUEST)
                // client errors
                5000 -> Pair(ErrorResponse(5000, "Problem calling Last.FM api"), HttpStatus.INTERNAL_SERVER_ERROR)
                5001 -> Pair(ErrorResponse(5001, "Problem saving track data"), HttpStatus.INTERNAL_SERVER_ERROR)
                else -> Pair(ErrorResponse(0, "Unknown error"), HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    var errorResponse: ErrorResponse by Delegates.notNull<ErrorResponse>()
    var status: HttpStatus by Delegates.notNull<HttpStatus>()

    constructor(code: Int) : super("") {
        val typeAndResponse = getTypeAndStatus(code)
        this.errorResponse = typeAndResponse.first
        this.status = typeAndResponse.second
    }

    constructor(code: Int, message: String) : super(message) {
        val typeAndResponse = getTypeAndStatus(code)
        this.errorResponse = typeAndResponse.first
        this.status = typeAndResponse.second
    }

    constructor(code: Int, cause: Throwable) : super(cause) {
        val typeAndResponse = getTypeAndStatus(code)
        this.errorResponse = typeAndResponse.first
        this.status = typeAndResponse.second
    }

    constructor(code: Int, message: String, cause: Throwable) : super(message, cause) {
        val typeAndResponse = getTypeAndStatus(code)
        this.errorResponse = typeAndResponse.first
        this.status = typeAndResponse.second
    }
}
