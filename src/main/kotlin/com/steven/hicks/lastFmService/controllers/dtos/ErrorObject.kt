package com.steven.hicks.lastFmService.controllers.dtos

class ErrorObject {
    private var errorCode: ErrorCode
    private var msg: String

    constructor(message: String) : this(message, ErrorCode.UNKNOWN_ERROR)
    constructor(message: String, e: ErrorCode) {
        msg = message
        errorCode = e
    }

    companion object {
        enum class ErrorCode(val code: Int) {
            UNKNOWN_ERROR(0);

            override fun toString(): String {
                return "$name $code"
            }
        }
    }
}
